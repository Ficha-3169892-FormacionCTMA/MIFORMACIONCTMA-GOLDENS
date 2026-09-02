package com.samuel.miformacionctma.model

import java.time.LocalDate

/**
 * Representa una entrada en la bitácora de seguimiento del aprendiz.
 */
data class Bitacora(
    val id: Long = 0,
    val fecha: LocalDate = LocalDate.now(),
    val titulo: String,
    val contenido: String,
    val horas: Int
)
