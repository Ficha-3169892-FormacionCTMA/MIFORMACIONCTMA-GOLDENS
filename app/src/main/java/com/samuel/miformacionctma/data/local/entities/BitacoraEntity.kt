package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "bitacoras")
data class BitacoraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val fecha: LocalDate,
    val titulo: String,
    val contenido: String,
    val horas: Int,
    val isSynced: Boolean = false
)
