package com.samuel.miformacionctma.ui

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.di.AppContainer
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.data.repository.InstructorStats
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.network.*
import com.samuel.miformacionctma.sync.SyncWorker
import com.samuel.miformacionctma.ui.screens.MediaSelectorState
import com.samuel.miformacionctma.util.EstadoActividad
import com.samuel.miformacionctma.util.UserRoles
import com.samuel.miformacionctma.util.calcularEstadoActividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
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
    val userEmail = userPrefs.userEmail.stateIn(viewModelScope, SharingStarted.Eagerly, null)
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
    val novedadesRecibidas = userId.flatMapLatest { id ->
        if (id != null) repository.getNovedadesRecibidas(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val certificados = userId.flatMapLatest { id ->
        if (id != null) repository.getCertificados(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val asignaciones = userId.flatMapLatest { id ->
        if (id != null) repository.getAsignacionesByAprendiz(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val actividadesConEstado: StateFlow<List<Pair<ActividadFormativa, EstadoActividad>>> = repository.getActividadesStream("")
        .combine(asignaciones) { listaActividades, listaAsignaciones ->
            val mapaAsignaciones = listaAsignaciones.associateBy { it.actividadId }
            listaActividades.map { actividad ->
                val asignacion = actividad.remoteId?.let { mapaAsignaciones[it] }
                val estado = calcularEstadoActividad(actividad.fechaFin, asignacion)
                Pair(actividad, estado)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dashboardStats: StateFlow<DashboardData> = actividadesConEstado.map { list ->
        val total = list.size
        val completadas = list.count { it.second == EstadoActividad.COMPLETADA }
        val fallidas = list.count { it.second == EstadoActividad.FALLIDA }
        val enRevision = list.count { it.second == EstadoActividad.EN_REVISION }
        val vencidas = list.count { it.second == EstadoActividad.VENCIDA }
        val pendientes = list.count { it.second == EstadoActividad.PENDIENTE }
        val progresoGral = if (total > 0) ((completadas.toDouble() / total) * 100).toInt() else 0

        DashboardData(
            totalActividades = total,
            completadas = completadas,
            enProceso = 0,
            pendientes = pendientes,
            vencidas = vencidas,
            fallidas = fallidas,
            enRevision = enRevision,
            progresoGeneral = progresoGral
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardData())

    private val _instructorStats = MutableStateFlow<InstructorStats?>(null)
    val instructorStats: StateFlow<InstructorStats?> = _instructorStats.asStateFlow()

    fun cargarEstadisticasInstructor() {
        val currentUserId = userId.value ?: return
        viewModelScope.launch {
            val result = container.revisionRepository.getEstadisticasInstructor(currentUserId)
            result.onSuccess { stats ->
                _instructorStats.value = stats
            }
        }
    }

    // --- Acciones del ViewModel ---

    fun cargarMediaDesdeUri(uri: Uri) {
        val app = getApplication<Application>()
        val contentResolver = app.contentResolver
        var nombre = "Evidencia_${System.currentTimeMillis()}.jpg"
        var size = 0L
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) cursor.getString(nameIndex)?.let { nombre = it }
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

        val fileUri: Uri = try {
            val dir = File(app.filesDir, "evidencias_locales")
            if (!dir.exists()) dir.mkdirs()
            val localFile = File(dir, "CAP_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                localFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (size == 0L) size = localFile.length()
            Uri.fromFile(localFile)
        } catch (e: Exception) {
            uri
        }

        if (size > 5 * 1024 * 1024) {
            _selectedMedia.value = MediaSelectorState(error = "El archivo excede el tamaño máximo permitido de 5MB")
            return
        }

        _selectedMedia.value = MediaSelectorState(
            uri = fileUri,
            nombre = nombre,
            mimeType = mimeType,
            tamanoBytes = size,
            error = null
        )
    }

    fun limpiarMedia() {
        _selectedMedia.value = null
    }

    fun guardarYEnviarEvidenciaMultimedia(actividadId: Long, comentario: String? = null) {
        val media = _selectedMedia.value ?: return
        if (media.error != null || media.uri == null) return

        viewModelScope.launch {
            try {
                repository.eliminarEvidenciaAnteriorYGuardarNueva(
                    actividadId = actividadId,
                    userId = userId.value ?: "unknown",
                    nombre = media.nombre,
                    uriString = media.uri.toString(),
                    mime = media.mimeType,
                    tamano = media.tamanoBytes,
                    comentario = comentario
                )
                
                _selectedMedia.value = null 
                SyncWorker.triggerOneTimeSync(getApplication())
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                // Manejo resiliente
            }
        }
    }

    fun actualizarComentarioEvidencia(evidenciaId: Long, actividadId: Long, nuevoComentario: String?) {
        val currentUserId = userId.value ?: return
        viewModelScope.launch {
            repository.actualizarComentarioEvidencia(evidenciaId, actividadId, currentUserId, nuevoComentario)
            SyncWorker.triggerOneTimeSync(getApplication())
        }
    }

    fun reintentarSincronizacionEvidencia(evidenciaId: Long) {
        viewModelScope.launch {
            SyncWorker.triggerOneTimeSync(getApplication())
        }
    }

    suspend fun getSignedUrlForEvidencia(storagePath: String): String {
        return repository.getSignedUrlForEvidencia(storagePath)
    }

    fun refresh() {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            SyncWorker.triggerOneTimeSync(getApplication())
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
        val currentInstructorId = userId.value ?: ""
        if (!UserRoles.isInstructor(userRole.value)) {
            _operacionState.value = OperacionUiState.Fallida(
                NetworkError.SinConexion,
                "Solo los instructores pueden crear actividades"
            )
            return
        }

        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                val nuevaActividad = ActividadFormativa(
                    id = 0L,
                    titulo = titulo,
                    descripcion = desc,
                    fechaInicio = fInicio,
                    fechaFin = fFin,
                    progreso = 0,
                    diasRestantes = 0,
                    prioridad = prior
                )
                repository.saveActividad(nuevaActividad, currentInstructorId)
                SyncWorker.triggerOneTimeSync(getApplication())
                _operacionState.value = OperacionUiState.Exitosa
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(NetworkError.Desconocido, e.message)
            }
        }
    }

    fun deleteActividad(actividad: ActividadEntity) {
        val currentUserId = userId.value ?: ""
        if (!UserRoles.isInstructor(userRole.value) || actividad.instructorId != currentUserId) {
            _operacionState.value = OperacionUiState.Fallida(
                NetworkError.SinConexion,
                "No puedes borrar esta actividad"
            )
            return
        }

        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            val result = repository.deleteActividad(actividad)
            result.fold(
                onSuccess = {
                    _operacionState.value = OperacionUiState.Exitosa
                },
                onFailure = { error ->
                    _operacionState.value = OperacionUiState.Fallida(
                        NetworkError.Desconocido,
                        error.message ?: "No puedes borrar esta actividad"
                    )
                }
            )
        }
    }

    fun deleteActividad(actividadFormativa: ActividadFormativa) {
        viewModelScope.launch {
            val entity = repository.getActividadEntityById(actividadFormativa.id)
            if (entity != null) {
                deleteActividad(entity)
            } else {
                _operacionState.value = OperacionUiState.Fallida(
                    NetworkError.SinConexion,
                    "No puedes borrar esta actividad"
                )
            }
        }
    }

    fun addNovedad(tipo: String, motivo: String, fecha: LocalDate, fechaFin: LocalDate? = null, adjunto: String?) {
        val currentUserId = userId.value ?: "unknown"
        val currentRole = userRole.value ?: "aprendiz"
        Log.d("DEBUG_NOVEDAD_ADD", "addNovedad: currentUserId=\"$currentUserId\", currentRole=\"$currentRole\", fecha=\"$fecha\"")
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                repository.saveNovedad(
                    NovedadEntity(
                        userId = currentUserId,
                        tipo = tipo,
                        motivo = motivo,
                        fecha = fecha,
                        fechaFin = fechaFin,
                        documentoAdjunto = adjunto,
                        tipoAutor = currentRole
                    )
                )
                SyncWorker.triggerOneTimeSync(getApplication())
                _operacionState.value = OperacionUiState.Exitosa
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(NetworkError.Desconocido, e.message)
            }
        }
    }

    fun scanQRAsistencia(qrContent: String) {
        val currentUserId = userId.value ?: return
        viewModelScope.launch {
            repository.registrarAsistencia(currentUserId, true, "QR: $qrContent")
            SyncWorker.triggerOneTimeSync(getApplication())
        }
    }

    fun addBitacora(titulo: String, contenido: String, horas: Int) {
        val currentUserId = userId.value ?: return
        viewModelScope.launch {
            repository.saveBitacora(
                BitacoraEntity(
                    autorId = currentUserId,
                    fecha = LocalDate.now(),
                    titulo = titulo,
                    contenido = contenido,
                    horas = horas
                )
            )
            SyncWorker.triggerOneTimeSync(getApplication())
        }
    }

    fun editarBitacora(id: Long, remoteId: Long?, fechaOriginal: LocalDate, titulo: String, contenido: String, horas: Int) {
        val currentUserId = userId.value ?: return
        viewModelScope.launch {
            repository.updateBitacora(
                BitacoraEntity(
                    id = id,
                    autorId = currentUserId,
                    fecha = fechaOriginal,
                    titulo = titulo,
                    contenido = contenido,
                    horas = horas,
                    isSynced = false,
                    remoteId = remoteId
                )
            )
            SyncWorker.triggerOneTimeSync(getApplication())
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
            SyncWorker.triggerOneTimeSync(getApplication())
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

    fun logout() {
        viewModelScope.launch {
            container.authRepository.signOut()
        }
    }
}
