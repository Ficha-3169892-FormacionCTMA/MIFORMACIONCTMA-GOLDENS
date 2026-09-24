package com.samuel.miformacionctma.model

import java.time.LocalDate

/**
 * Registro de asistencia a una sesión formativa.
 */
data class Asistencia(
    val id: Long = 0,
    val fecha: LocalDate = LocalDate.now(),
    val estuvoPresente: Boolean = true,
    val observacion: String? = null
)
