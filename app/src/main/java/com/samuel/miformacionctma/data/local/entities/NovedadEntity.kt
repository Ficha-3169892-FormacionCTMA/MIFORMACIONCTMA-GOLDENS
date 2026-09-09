package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "novedades")
data class NovedadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val tipo: String, // "MEDICA", "PERMISO", "OTRA"
    val motivo: String,
    val fecha: LocalDate,
    val documentoAdjunto: String?,
    val estado: String = "PENDIENTE", // "PENDIENTE", "REVISION", "APROBADA", "RECHAZADA"
    val isSynced: Boolean = false
)
