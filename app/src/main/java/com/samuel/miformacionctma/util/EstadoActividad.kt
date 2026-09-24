package com.samuel.miformacionctma.util

import com.samuel.miformacionctma.data.local.entities.AsignacionActividadEntity
import java.time.LocalDate

enum class EstadoActividad { PENDIENTE, EN_REVISION, COMPLETADA, FALLIDA, VENCIDA }

fun calcularEstadoActividad(
    fechaFin: LocalDate,
    asignacion: AsignacionActividadEntity?
): EstadoActividad {
    return when {
        asignacion?.estado == "completada" -> EstadoActividad.COMPLETADA
        asignacion?.estado == "fallida" -> EstadoActividad.FALLIDA
        asignacion?.estado == "en_revision" -> EstadoActividad.EN_REVISION
        LocalDate.now().isAfter(fechaFin) -> EstadoActividad.VENCIDA
        else -> EstadoActividad.PENDIENTE
    }
}
