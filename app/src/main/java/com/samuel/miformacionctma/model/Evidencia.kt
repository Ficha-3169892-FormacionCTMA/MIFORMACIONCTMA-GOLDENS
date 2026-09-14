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
    val comentarioAprendiz: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SINCRONIZADO,
    val evidenciaUri: String = "",
    val mimeType: String = "",
    val tamanoBytes: Long = 0L
)
