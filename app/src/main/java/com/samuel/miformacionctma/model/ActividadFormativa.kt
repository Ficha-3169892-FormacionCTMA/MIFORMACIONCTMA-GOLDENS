package com.samuel.miformacionctma.model

import java.time.LocalDate

data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad
) {
    // El progreso real de dominio siempre estará acotado entre 0 y 100
    val progresoAcotado: Int = progreso.coerceIn(0, 100)

    init {
        require(titulo.isNotBlank()) { "El título no puede estar vacío" }
    }
}
