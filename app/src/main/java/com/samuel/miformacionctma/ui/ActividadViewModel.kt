package com.samuel.miformacionctma.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.formulario.FormularioActividadEvento
import com.samuel.miformacionctma.ui.formulario.FormularioActividadUiState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ActividadViewModel : ViewModel() {

    // Lista de actividades (Estado global de la app en este ejemplo)
    var actividades by mutableStateOf(
        listOf(
            ActividadFormativa(
                id = 1,
                titulo = "Diseño de interfaz",
                descripcion = "Crear la pantalla principal",
                fechaInicio = LocalDate.now().minusDays(1),
                fechaFin = LocalDate.now().plusDays(2),
                progreso = 80,
                diasRestantes = 2,
                prioridad = Prioridad.ALTA
            )
        )
    )
        private set

    // Estado del formulario
    var uiState by mutableStateOf(FormularioActividadUiState())
        private set

    var guardando by mutableStateOf(false)
        private set

    fun onEvento(evento: FormularioActividadEvento) {
        when (evento) {
            is FormularioActividadEvento.TituloCambiado -> {
                uiState = uiState.copy(titulo = evento.valor)
            }
            is FormularioActividadEvento.DescripcionCambiada -> {
                uiState = uiState.copy(descripcion = evento.valor)
            }
            is FormularioActividadEvento.FechaInicioCambiada -> {
                uiState = uiState.copy(fechaInicio = evento.valor)
            }
            is FormularioActividadEvento.FechaFinCambiada -> {
                uiState = uiState.copy(fechaFin = evento.valor)
            }
            is FormularioActividadEvento.ProgresoCambiado -> {
                uiState = uiState.copy(progreso = evento.valor)
            }
            FormularioActividadEvento.Guardar -> guardarActividad()
            FormularioActividadEvento.Cancelar -> limpiarFormulario()
        }
    }

    private fun guardarActividad() {
        if (uiState.esValido) {
            guardando = true
            
            // Simulación de guardado
            val nueva = ActividadFormativa(
                id = System.currentTimeMillis(),
                titulo = uiState.titulo,
                descripcion = uiState.descripcion,
                fechaInicio = LocalDate.parse(uiState.fechaInicio),
                fechaFin = LocalDate.parse(uiState.fechaFin),
                progreso = uiState.progreso.toInt(),
                diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(uiState.fechaFin)).toInt(),
                prioridad = Prioridad.MEDIA // Por defecto para el ejemplo
            )
            
            actividades = actividades + nueva
            limpiarFormulario()
            guardando = false
        }
    }

    private fun limpiarFormulario() {
        uiState = FormularioActividadUiState()
    }
    
    fun buscarPorId(id: Long): ActividadFormativa? {
        return actividades.find { it.id == id }
    }
}