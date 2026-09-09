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
)