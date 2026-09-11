package com.samuel.miformacionctma.data.remote.dto

import kotlinx.serialization.Serializable
import com.samuel.miformacionctma.data.local.entities.ActividadEntity
import com.samuel.miformacionctma.model.Prioridad
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class ActividadDto(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val fechaInicio: String, // ISO format
    val fechaFin: String,    // ISO format
    val progreso: Int,
    val prioridad: String,
    val instructorId: String
)

fun ActividadDto.toEntity(): ActividadEntity {
    return ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaInicio = LocalDate.parse(fechaInicio),
        fechaFin = LocalDate.parse(fechaFin),
        progreso = progreso,
        prioridad = try { Prioridad.valueOf(prioridad.uppercase()) } catch (e: Exception) { Prioridad.BAJA },
        instructorId = instructorId,
        isSynced = true
    )
}
