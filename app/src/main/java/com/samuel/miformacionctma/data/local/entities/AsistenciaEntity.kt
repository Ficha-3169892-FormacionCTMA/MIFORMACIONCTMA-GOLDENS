package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "asistencia")
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val fecha: LocalDate,
    val estuvoPresente: Boolean,
    val observacion: String?,
    val isSynced: Boolean = false
)
