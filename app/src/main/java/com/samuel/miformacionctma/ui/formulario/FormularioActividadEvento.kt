package com.samuel.miformacionctma.ui.formulario

sealed interface FormularioActividadEvento {
    data class TituloCambiado(val valor: String) : FormularioActividadEvento
    data class DescripcionCambiada(val valor: String) : FormularioActividadEvento
    data class FechaInicioCambiada(val valor: String) : FormularioActividadEvento
    data class FechaFinCambiada(val valor: String) : FormularioActividadEvento
    data class ProgresoCambiado(val valor: String) : FormularioActividadEvento
    data object Guardar : FormularioActividadEvento
    data object Cancelar : FormularioActividadEvento
}