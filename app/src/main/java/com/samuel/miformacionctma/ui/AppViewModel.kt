package com.samuel.miformacionctma.ui

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.di.AppContainer
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.network.*
import com.samuel.miformacionctma.ui.screens.MediaSelectorState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class AppViewModel(
    application: Application,
    private val container: AppContainer
) : AndroidViewModel(application) {

    private val repository: AppRepository = container.appRepository
    private val userPrefs: UserPreferencesRepository = container.userPreferencesRepository

    // --- Sesión de Usuario ---
    val userId = userPrefs.userId.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userRole = userPrefs.userRole.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userName = userPrefs.userName.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val themeMode = userPrefs.themeMode.stateIn(viewModelScope, SharingStarted.Eagerly, "SYSTEM")
    val fontSizeScale = userPrefs.fontSizeScale.stateIn(viewModelScope, SharingStarted.Eagerly, "MEDIUM")
    val notificacionesEnabled = userPrefs.notificacionesEnabled.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    
    val filtroPrioridad = userPrefs.filtroPrioridad.stateIn(
        viewModelScope, 
        SharingStarted.WhileSubscribed(5_000), 
        "TODAS"
    )

    // --- Estados de UI ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _operacionState = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val operacionState = _operacionState.asStateFlow()

    private val _lastUpdate = MutableStateFlow<LocalDateTime?>(null)
    val lastUpdate = _lastUpdate.asStateFlow()

    // --- Estado para el flujo de captura/selección multimedia (Semana 9) ---
    private val _selectedMedia = MutableStateFlow<MediaSelectorState?>(null)
    val selectedMedia = _selectedMedia.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ListadoUiState> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            repository.getActividadesStream(query)
        }
        .combine(userPrefs.filtroPrioridad) { actividades, filtro ->
            val filtradas = if (filtro == "TODAS") {
                actividades
            } else {
                actividades.filter { it.prioridad.name == filtro }
            }

            if (filtradas.isEmpty()) {
                ListadoUiState.Vacio
            } else {
                ListadoUiState.Contenido(filtradas)
            }
        }
        .onStart { emit(ListadoUiState.Cargando) }
        .catch { e ->
            emit(ListadoUiState.Error(e.message ?: "Error al cargar datos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListadoUiState.Cargando
        )

    val actividades = repository.getActividadesStream("")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val asistencias = userId.flatMapLatest { id ->
        if (id != null) repository.getAsistencias(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val bitacoras = userId.flatMapLatest { id ->
        if (id != null) repository.getBitacoras(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val novedades = userId.flatMapLatest { id ->
        if (id != null) repository.getNovedades(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val certificados = userId.flatMapLatest { id ->
        if (id != null) repository.getCertificados(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dashboardStats = repository.getActividadesStream("").map { list ->
        val total = list.size
        val completadas = list.count { it.progreso == 100 }
        val enProceso = list.count { it.progreso in 1..99 }
        val pendientes = list.count { it.progreso == 0 }
        val vencidas = list.count { it.fechaFin.isBefore(LocalDate.now()) && it.progreso < 100 }
        val progresoGral = if (total > 0) (list.sumOf { it.progreso }.toDouble() / total).toInt() else 0
        
        DashboardData(total, completadas, enProceso, pendientes, vencidas, progresoGral)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardData())

    // --- Acciones del ViewModel ---

    fun cargarMediaDesdeUri(uri: Uri) {
        val contentResolver = getApplication<Application>().contentResolver
        var nombre = "Evidencia_${System.currentTimeMillis()}"
        var size = 0L
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) nombre = cursor.getString(nameIndex) ?: nombre
                    if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
                }
            }
        } catch (e: Exception) {
            // Fallback
        }

        if (!mimeType.startsWith("image/")) {
            _selectedMedia.value = MediaSelectorState(error = "Tipo de archivo no válido. Solo se admiten imágenes.")
            return
        }
        if (size > 5 * 1024 * 1024) {
            _selectedMedia.value = MediaSelectorState(error = "El archivo excede el tamaño máximo permitido de 5MB")
            return
        }

        _selectedMedia.value = MediaSelectorState(
            uri = uri,
            nombre = nombre,
            mimeType = mimeType,
            tamanoBytes = size,
            error = null
        )
    }

    fun limpiarMedia() {
        _selectedMedia.value = null
    }

    fun guardarYEnviarEvidenciaMultimedia(actividadId: Long) {
        val media = _selectedMedia.value ?: return
        if (media.error != null || media.uri == null) return

        viewModelScope.launch {
            try {
                val evidenciaId = repository.guardarEvidenciaLocal(
                    actividadId = actividadId,
                    userId = userId.value ?: "unknown",
                    nombre = media.nombre,
                    uriString = media.uri.toString(),
                    mime = media.mimeType,
                    tamano = media.tamanoBytes
                )
                
                _selectedMedia.value = null 
                
                when (val result = repository.sincronizarEvidenciaConServidor(evidenciaId)) {
                    is NetworkResult.Success -> {
                        repository.actualizarEstadoSincronizacion(evidenciaId, "SINCRONIZADA", result.data)
                    }
                    is NetworkResult.Error -> {
                        repository.actualizarEstadoSincronizacion(evidenciaId, "FALLIDA")
                    }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                // Manejo resiliente
            }
        }
    }

    fun reintentarSincronizacionEvidencia(evidenciaId: Long) {
        viewModelScope.launch {
            when (val result = repository.sincronizarEvidenciaConServidor(evidenciaId)) {
                is NetworkResult.Success -> {
                    repository.actualizarEstadoSincronizacion(evidenciaId, "SINCRONIZADA", result.data)
                }
                is NetworkResult.Error -> {
                    repository.actualizarEstadoSincronizacion(evidenciaId, "FALLIDA")
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            when (val result = repository.refreshActividades()) {
                is NetworkResult.Success -> {
                    _operacionState.value = OperacionUiState.Exitosa
                    _lastUpdate.value = LocalDateTime.now()
                }
                is NetworkResult.Error -> {
                    _operacionState.value = OperacionUiState.Fallida(result.errorType, result.message)
                }
            }
        }
    }

    fun addActividad(titulo: String, desc: String, fInicio: LocalDate, fFin: LocalDate, prior: Prioridad) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                val nuevaActividad = ActividadFormativa(
                    id = System.currentTimeMillis(),
                    titulo = titulo,
                    descripcion = desc,
                    fechaInicio = fInicio,
                    fechaFin = fFin,
                    progreso = 0,
                    diasRestantes = 0,
                    prioridad = prior
                )
                repository.saveActividad(nuevaActividad)
                _operacionState.value = OperacionUiState.Exitosa
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(NetworkError.Desconocido, e.message)
            }
        }
    }

    fun deleteActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                repository.deleteActividad(actividad)
                _operacionState.value = OperacionUiState.Exitosa
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(NetworkError.Desconocido, e.message)
            }
        }
    }

    fun addNovedad(tipo: String, motivo: String, fecha: LocalDate, adjunto: String?) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                repository.saveNovedad(
                    NovedadEntity(
                        userId = userId.value ?: "unknown",
                        tipo = tipo,
                        motivo = motivo,
                        fecha = fecha,
                        documentoAdjunto = adjunto
                    )
                )
                _operacionState.value = OperacionUiState.Exitosa
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(NetworkError.Desconocido, e.message)
            }
        }
    }

    fun scanQRAsistencia(qrContent: String) {
        viewModelScope.launch {
            repository.registrarAsistencia(userId.value ?: "", true, "QR: $qrContent")
        }
    }

    fun addBitacora(titulo: String, contenido: String, horas: Int) {
        viewModelScope.launch {
            repository.saveBitacora(
                BitacoraEntity(
                    userId = userId.value ?: "",
                    fecha = LocalDate.now(),
                    titulo = titulo,
                    contenido = contenido,
                    horas = horas
                )
            )
        }
    }

    fun submitEvidencia(actividadId: Long, url: String) {
        viewModelScope.launch {
            repository.saveEvidencia(
                EvidenciaEntity(
                    actividadId = actividadId,
                    userId = userId.value ?: "",
                    nombreArchivo = "Evidencia_${System.currentTimeMillis()}",
                    url = url,
                    fechaEntrega = LocalDateTime.now(),
                    comentarioAprendiz = null
                )
            )
        }
    }

    fun getEvidencias(actividadId: Long) = repository.getEvidencias(actividadId)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterPrioridad(prioridad: String) {
        viewModelScope.launch {
            userPrefs.setFiltroPrioridad(prioridad)
        }
    }

    fun resetOperacionState() {
        _operacionState.value = OperacionUiState.Inactiva
    }

    fun updateTheme(mode: String) { viewModelScope.launch { userPrefs.setThemeMode(mode) } }
    fun updateFontSize(scale: String) { viewModelScope.launch { userPrefs.setFontSizeScale(scale) } }
    fun setNotificacionesEnabled(enabled: Boolean) { viewModelScope.launch { userPrefs.setNotificacionesEnabled(enabled) } }

    fun login(email: String, role: String, name: String) {
        viewModelScope.launch {
            val id = email.split("@")[0]
            userPrefs.saveUser(id, role, "MOCK_TOKEN", name)
        }
    }

    fun logout() {
        viewModelScope.launch { userPrefs.clearUser() }
    }
}
