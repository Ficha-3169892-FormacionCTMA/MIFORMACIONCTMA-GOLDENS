package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "evidencias")
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Long,
    val userId: String,
    val nombreArchivo: String,
    val url: String,
    val fechaEntrega: LocalDateTime,
    val comentarioAprendiz: String?,
    val isSynced: Boolean = false
)
