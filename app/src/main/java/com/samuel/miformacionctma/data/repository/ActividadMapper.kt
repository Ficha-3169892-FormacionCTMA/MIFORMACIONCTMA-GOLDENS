package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.data.local.entities.ActividadEntity
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.network.ActividadDto
import java.time.LocalDate

fun ActividadDto.toEntity(): ActividadEntity = ActividadEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fechaInicio = LocalDate.parse(fechaInicio),
    fechaFin = LocalDate.parse(fechaFin),
    progreso = progreso,
    prioridad = Prioridad.valueOf(prioridad.uppercase()),
    instructorId = "remote_sync", // Or map from DTO if available
    isSynced = true
)

fun ActividadEntity.toDto(): ActividadDto = ActividadDto(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fechaInicio = fechaInicio.toString(),
    fechaFin = fechaFin.toString(),
    progreso = progreso,
    prioridad = prioridad.name
)
