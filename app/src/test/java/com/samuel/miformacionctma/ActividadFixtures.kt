package com.samuel.miformacionctma

import com.samuel.miformacionctma.data.local.entities.ActividadEntity
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.network.ActividadDto
import com.samuel.miformacionctma.network.ActividadSupabaseDto
import java.time.LocalDate

fun createActividadSupabaseDto(
    id: Long? = 1L,
    titulo: String = "Actividad de Prueba",
    descripcion: String? = "Descripción de prueba",
    fechaInicio: String = "2024-01-01",
    fechaFin: String = "2024-01-10",
    progreso: Int = 0,
    prioridad: String = "MEDIA",
    instructorId: String = "inst_01"
) = ActividadSupabaseDto(id, titulo, descripcion, fechaInicio, fechaFin, progreso, prioridad, instructorId)

fun createActividadDto(
    id: Long = 1L,
    titulo: String = "Actividad de Prueba",
    descripcion: String? = "Descripción de prueba",
    fechaInicio: String = "2024-01-01",
    fechaFin: String = "2024-01-10",
    progreso: Int = 0,
    prioridad: String = "MEDIA"
) = ActividadDto(id, titulo, descripcion, fechaInicio, fechaFin, progreso, prioridad)

fun createActividadEntity(
    id: Long = 1L,
    titulo: String = "Actividad Entity",
    descripcion: String? = "Desc",
    fechaInicio: LocalDate = LocalDate.now(),
    fechaFin: LocalDate = LocalDate.now().plusDays(7),
    progreso: Int = 0,
    prioridad: Prioridad = Prioridad.MEDIA,
    instructorId: String = "inst_01",
    isSynced: Boolean = true
) = ActividadEntity(id, titulo, descripcion, fechaInicio, fechaFin, progreso, prioridad, instructorId, isSynced)

fun createActividadDominio(
    id: Long = 1L,
    titulo: String = "Actividad Dominio",
    descripcion: String? = "Desc",
    fechaInicio: LocalDate = LocalDate.now(),
    fechaFin: LocalDate = LocalDate.now().plusDays(7),
    progreso: Int = 0,
    diasRestantes: Int = 7,
    prioridad: Prioridad = Prioridad.MEDIA
) = ActividadFormativa(id, titulo, descripcion, fechaInicio, fechaFin, progreso, diasRestantes, prioridad)
