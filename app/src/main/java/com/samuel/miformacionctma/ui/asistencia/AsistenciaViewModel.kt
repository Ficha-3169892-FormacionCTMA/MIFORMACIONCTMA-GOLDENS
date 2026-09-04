package com.samuel.miformacionctma.ui.asistencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AsistenciaViewModel(
    private val repository: AppRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AsistenciaUiState())
    val uiState: StateFlow<AsistenciaUiState> = _uiState.asStateFlow()

    init {
        onEvent(AsistenciaEvent.CargarAsistencias)
    }

    fun onEvent(event: AsistenciaEvent) {
        when (event) {
            is AsistenciaEvent.CargarAsistencias -> cargarAsistencias()
            is AsistenciaEvent.RegistrarAsistencia -> registrar(event.presente, event.observacion)
            AsistenciaEvent.ErrorConsumido -> _uiState.update { it.copy(error = null) }
            AsistenciaEvent.MensajeExitoConsumido -> _uiState.update { it.copy(mensajeExito = null) }
        }
    }

    private fun cargarAsistencias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAsistencias(userId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { lista ->
                    _uiState.update { it.copy(isLoading = false, asistencias = lista) }
                }
        }
    }

    private fun registrar(presente: Boolean, observacion: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrando = true) }
            try {
                repository.registrarAsistencia(userId, presente, observacion)
                _uiState.update { 
                    it.copy(isRegistrando = false, mensajeExito = "Asistencia registrada correctamente") 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isRegistrando = false, error = "Error al registrar: ${e.message}") 
                }
            }
        }
    }
}
