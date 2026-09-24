package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asignacion_actividad")
data class AsignacionActividadEntity(
    @PrimaryKey val id: Long,
    val actividadId: Long,
    val aprendizId: String,
    val fechaAsignacion: String,
    val estado: String = "pendiente"
)
