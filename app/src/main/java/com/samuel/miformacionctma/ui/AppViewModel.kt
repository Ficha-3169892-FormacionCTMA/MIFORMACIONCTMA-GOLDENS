package com.samuel.miformacionctma.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.data.remote.RemoteActividadDataSource
import com.samuel.miformacionctma.data.remote.TokenProvider
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * ViewModel que gestiona el estado reactivo de la aplicación usando Flow y StateFlow.
 * Implementa la lógica de filtrado, búsqueda y manejo de operaciones asíncronas.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val userPrefs: UserPreferencesRepository

    init {
        val db = AppDatabase.getDatabase(application)
        val tokenProvider = TokenProvider()
        val remoteDataSource = RemoteActividadDataSource(tokenProvider)
        repository = AppRepository(db, remoteDataSource)
        userPrefs = UserPreferencesRepository(application)
        
        // Sincronización inicial
        refreshActividades()
    }

    // --- Sesión de Usuario ---
    val userId = userPrefs.userId.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userRole = userPrefs.userRole.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userName = userPrefs.userName.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val themeMode = userPrefs.themeMode.stateIn(viewModelScope, SharingStarted.Eagerly, "SYSTEM")
    val fontSizeScale = userPrefs.fontSizeScale.stateIn(viewModelScope, SharingStarted.Eagerly, "MEDIUM")
    
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

    /**
     * Estado reactivo del listado de actividades (Semana 7).
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
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

    // --- Propiedades requeridas por otras pantallas ---

    val actividades = repository.getActividadesStream("")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val asistencias = userId.flatMapLatest { id ->
        if (id != null) repository.getAsistencias(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val bitacoras = userId.flatMapLatest { id ->
        if (id != null) repository.getBitacoras(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val novedades = userId.flatMapLatest { id ->
        if (id != null) repository.getNovedades(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    fun refreshActividades() {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                repository.refreshActividades()
                _operacionState.value = OperacionUiState.Exitosa
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(e.message ?: "Error al sincronizar")
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
                _operacionState.value = OperacionUiState.Fallida(e.message ?: "Error al guardar")
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
                _operacionState.value = OperacionUiState.Fallida(e.message ?: "Error al eliminar")
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

    fun addNovedad(tipo: String, motivo: String, fecha: LocalDate, adjunto: String?) {
        viewModelScope.launch {
            // repository.saveNovedad(NovedadEntity(...)) 
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

data class DashboardData(
    val totalActividades: Int = 0,
    val completadas: Int = 0,
    val enProceso: Int = 0,
    val pendientes: Int = 0,
    val vencidas: Int = 0,
    val progresoGeneral: Int = 0
)
