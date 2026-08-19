package com.samuel.miformacionctma.ui.actividad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.formulario.FormularioActividadEvento
import com.samuel.miformacionctma.ui.formulario.FormularioActividadUiState
import com.samuel.miformacionctma.ui.screens.obtenerActividadesDePrueba
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActividadViewModel : ViewModel() {

    // Lista de actividades (Simulando una base de datos)
    private val _actividades = MutableStateFlow(obtenerActividadesDePrueba())
    val actividades: StateFlow<List<ActividadFormativa>> = _actividades.asStateFlow()

    // Estado del formulario
    var formUiState by mutableStateOf(FormularioActividadUiState())
        private set

    // Estado de carga/guardado
    var guardando by mutableStateOf(false)
        private set

    fun onFormEvento(evento: FormularioActividadEvento) {
        when (evento) {
            is FormularioActividadEvento.TituloCambiado -> {
                formUiState = formUiState.copy(titulo = evento.valor)
            }
            is FormularioActividadEvento.DescripcionCambiada -> {
                formUiState = formUiState.copy(descripcion = evento.valor)
            }
            is FormularioActividadEvento.FechaInicioCambiada -> {
                formUiState = formUiState.copy(fechaInicio = evento.valor)
            }
            is FormularioActividadEvento.FechaFinCambiada -> {
                formUiState = formUiState.copy(fechaFin = evento.valor)
            }
            is FormularioActividadEvento.ProgresoCambiado -> {
                formUiState = formUiState.copy(progreso = evento.valor)
            }
            FormularioActividadEvento.Guardar -> {
                if (formUiState.esValido && !guardando) {
                    guardarActividad()
                }
            }
            FormularioActividadEvento.Cancelar -> {
                resetForm()
            }
        }
    }

    private fun guardarActividad() {
        viewModelScope.launch {
            guardando = true
            // Simular delay de red/DB
            delay(1000)

            val nuevaActividad = ActividadFormativa(
                id = System.currentTimeMillis(),
                titulo = formUiState.titulo,
                descripcion = formUiState.descripcion.ifBlank { null },
                fechaInicio = LocalDate.parse(formUiState.fechaInicio),
                fechaFin = LocalDate.parse(formUiState.fechaFin),
                progreso = formUiState.progreso.toIntOrNull() ?: 0,
                diasRestantes = 7, // Valor por defecto o calculado
                prioridad = Prioridad.MEDIA // Podría agregarse al formulario
            )

            _actividades.update { actuales -> actuales + nuevaActividad }
            resetForm()
            guardando = false
            
            // Aquí se dispararía un evento de navegación hacia atrás en una app real
            // por simplicidad manejaremos la navegación en el NavHost
            _navegarAtras.value = true
        }
    }

    private val _navegarAtras = MutableStateFlow(false)
    val navegarAtras: StateFlow<Boolean> = _navegarAtras.asStateFlow()

    fun resetNavegarAtras() {
        _navegarAtras.value = false
    }

    private fun resetForm() {
        formUiState = FormularioActividadUiState()
    }

    fun obtenerActividadPorId(id: Long): ActividadFormativa? {
        return _actividades.value.find { it.id == id }
    }
}
