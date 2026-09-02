package com.samuel.miformacionctma.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.model.Prioridad
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val userPrefs: UserPreferencesRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db)
        userPrefs = UserPreferencesRepository(application)
    }

    // --- User Session (HU-06) ---
    val userId = userPrefs.userId.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userRole = userPrefs.userRole.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userName = userPrefs.userName.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val themeMode = userPrefs.themeMode.stateIn(viewModelScope, SharingStarted.Eagerly, "SYSTEM")
    val fontSizeScale = userPrefs.fontSizeScale.stateIn(viewModelScope, SharingStarted.Eagerly, "MEDIUM")

    // --- Actividades (HU-01, HU-02, HU-09, HU-12) ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterPrioridad = MutableStateFlow<Prioridad?>(null)
    val filterPrioridad = _filterPrioridad.asStateFlow()

    private val _filterEstado = MutableStateFlow<String?>(null) 
    val filterEstado = _filterEstado.asStateFlow()

    val actividades = repository.getActividades().combine(searchQuery) { list, query ->
        list.filter { it.titulo.contains(query, ignoreCase = true) || it.descripcion?.contains(query, ignoreCase = true) == true }
    }.combine(filterPrioridad) { list, prioridad ->
        if (prioridad == null) list else list.filter { it.prioridad == prioridad }
    }.combine(filterEstado) { list, estado ->
        if (estado == null) list else list.filter {
            when (estado) {
                "COMPLETADA" -> it.progreso == 100
                "EN_PROCESO" -> it.progreso in 1..99
                "PENDIENTE" -> it.progreso == 0
                else -> true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getEvidencias(actividadId: Long) = repository.getEvidencias(actividadId)

    // --- Dashboard Stats (HU-07) ---
    val dashboardStats = actividades.map { list ->
        val total = list.size
        val completadas = list.count { it.progreso == 100 }
        val enProceso = list.count { it.progreso in 1..99 }
        val pendientes = list.count { it.progreso == 0 }
        val vencidas = list.count { it.fechaFin.isBefore(LocalDate.now()) && it.progreso < 100 }
        val progresoGral = if (total > 0) (list.sumOf { it.progreso }.toDouble() / total).toInt() else 0
        
        DashboardData(total, completadas, enProceso, pendientes, vencidas, progresoGral)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardData())

    // --- Bitacoras (HU-04) ---
    val bitacoras = userId.flatMapLatest { id ->
        if (id != null) repository.getBitacoras(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Novedades (HU-14) ---
    val novedades = userId.flatMapLatest { id ->
        if (id != null) repository.getNovedades(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Certificados (HU-13) ---
    val certificados = userId.flatMapLatest { id ->
        if (id != null) repository.getCertificados(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Asistencia (HU-05) ---
    val asistencias = userId.flatMapLatest { id ->
        if (id != null) repository.getAsistencias(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---
    fun login(email: String, role: String, name: String) {
        viewModelScope.launch {
            val id = email.split("@")[0]
            userPrefs.saveUser(id, role, "MOCK_TOKEN", name)
            repository.saveUser(UserEntity(id, name, email, role, "DOC_$id"))
            
            // Seed some data for demo if empty
            // repository.saveActividad(...)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
        }
    }

    fun addActividad(titulo: String, desc: String, fInicio: LocalDate, fFin: LocalDate, prior: Prioridad) {
        viewModelScope.launch {
            repository.saveActividad(
                ActividadEntity(
                    id = System.currentTimeMillis(),
                    titulo = titulo,
                    descripcion = desc,
                    fechaInicio = fInicio,
                    fechaFin = fFin,
                    progreso = 0,
                    prioridad = prior,
                    instructorId = userId.value ?: "SYSTEM"
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

    fun scanQRAsistencia(qrContent: String) {
        viewModelScope.launch {
            repository.registrarAsistencia(userId.value ?: "", true, "QR: $qrContent")
        }
    }

    fun addNovedad(tipo: String, motivo: String, fecha: LocalDate, adjunto: String?) {
        viewModelScope.launch {
            repository.saveNovedad(
                NovedadEntity(
                    userId = userId.value ?: "",
                    tipo = tipo,
                    motivo = motivo,
                    fecha = fecha,
                    documentoAdjunto = adjunto
                )
            )
        }
    }

    fun updateTheme(mode: String) {
        viewModelScope.launch { userPrefs.setThemeMode(mode) }
    }

    fun updateFontSize(scale: String) {
        viewModelScope.launch { userPrefs.setFontSizeScale(scale) }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setFilterPrioridad(p: Prioridad?) { _filterPrioridad.value = p }
    fun setFilterEstado(e: String?) { _filterEstado.value = e }
}

data class DashboardData(
    val totalActividades: Int = 0,
    val completadas: Int = 0,
    val enProceso: Int = 0,
    val pendientes: Int = 0,
    val vencidas: Int = 0,
    val progresoGeneral: Int = 0
)
