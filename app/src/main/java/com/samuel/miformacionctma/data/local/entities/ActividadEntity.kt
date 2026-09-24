package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samuel.miformacionctma.model.Prioridad
import java.time.LocalDate

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String?,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val progreso: Int,
    val prioridad: Prioridad,
    val instructorId: String,
    val isSynced: Boolean = true,
    val remoteId: Long? = null
)
