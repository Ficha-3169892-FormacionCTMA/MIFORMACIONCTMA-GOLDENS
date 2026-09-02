package com.samuel.miformacionctma.model

import java.time.LocalDateTime

/**
 * Representa una evidencia entregada por el aprendiz.
 */
data class Evidencia(
    val id: Long = 0,
    val actividadId: Long,
    val nombreArchivo: String,
    val url: String,
    val fechaEntrega: LocalDateTime = LocalDateTime.now(),
    val comentarioAprendiz: String? = null
)
