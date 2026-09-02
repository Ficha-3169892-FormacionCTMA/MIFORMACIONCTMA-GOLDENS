package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "certificados")
data class CertificadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val nombre: String,
    val tipo: String, // "ASISTENCIA", "NOTAS"
    val url: String,
    val fechaEmision: String
)
