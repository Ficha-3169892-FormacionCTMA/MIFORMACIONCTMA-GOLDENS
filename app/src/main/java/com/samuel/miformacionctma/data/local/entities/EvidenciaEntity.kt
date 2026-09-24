package com.samuel.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["actividadId"])]
)
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Long,
    val userId: String,
    val nombreArchivo: String,
    val url: String,
    val fechaEntrega: LocalDateTime,
    val comentarioAprendiz: String?,
    val isSynced: Boolean = false,
    
    // Campos añadidos en Semana 9 (Principio de Mínimo Privilegio - No se almacena la imagen en binario)
    val evidenciaUri: String = "",
    val mimeType: String = "",
    val tamanoBytes: Long = 0L,
    val estadoSincronizacion: String = "LOCAL", // LOCAL, SUBIENDO, SINCRONIZADA, FALLIDA
    val storagePath: String = ""
)
