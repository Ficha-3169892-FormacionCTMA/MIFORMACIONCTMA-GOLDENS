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
    val prioridad: Prioridad,
    val syncStatus: SyncStatus = SyncStatus.SINCRONIZADO
) {
    init {
        require(titulo.isNotBlank()) { "El título no puede estar vacío" }
        require(progreso in 0..100) { "El progreso debe estar entre 0 y 100" }
    }
}
