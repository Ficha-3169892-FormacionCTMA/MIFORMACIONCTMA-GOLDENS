package com.samuel.miformacionctma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.data.repository.EntregaParaRevision
import com.samuel.miformacionctma.data.repository.RevisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RevisionUiState {
    data object Cargando : RevisionUiState
    data class Contenido(val lista: List<EntregaParaRevision>) : RevisionUiState
    data class Error(val mensaje: String) : RevisionUiState
}

class RevisionViewModel(
    private val repository: RevisionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RevisionUiState>(RevisionUiState.Cargando)
    val uiState: StateFlow<RevisionUiState> = _uiState.asStateFlow()

    private val _conteosEnRevision = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val conteosEnRevision: StateFlow<Map<Long, Int>> = _conteosEnRevision.asStateFlow()

    fun cargarEntregas(actividadRemoteId: Long) {
        viewModelScope.launch {
            _uiState.value = RevisionUiState.Cargando
            val result = repository.getEntregasParaActividad(actividadRemoteId)
            result.fold(
                onSuccess = { lista ->
                    _uiState.value = RevisionUiState.Contenido(lista)
                },
                onFailure = { error ->
                    _uiState.value = RevisionUiState.Error(
                        error.message ?: "Error al cargar las entregas para revisión"
                    )
                }
            )
        }
    }

    fun cargarConteosEnRevision(instructorId: String) {
        viewModelScope.launch {
            val result = repository.getConteosEnRevisionPorInstructor(instructorId)
            result.onSuccess { map ->
                _conteosEnRevision.value = map
            }
        }
    }

    fun marcarEstado(actividadRemoteId: Long, asignacionId: Long, nuevoEstado: String) {
        viewModelScope.launch {
            val result = repository.actualizarEstadoAsignacion(asignacionId, nuevoEstado)
            result.fold(
                onSuccess = {
                    cargarEntregas(actividadRemoteId)
                },
                onFailure = { error ->
                    _uiState.value = RevisionUiState.Error(
                        error.message ?: "No se pudo actualizar el estado de la entrega"
                    )
                }
            )
        }
    }
}
