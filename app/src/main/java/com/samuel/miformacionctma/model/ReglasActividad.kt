package com.samuel.miformacionctma.model

import java.time.LocalDate

/**
 * Calcula el estado textual de una actividad basado en el progreso y fechas.
 * Utiliza constantes en MAYÚSCULAS para consistencia en toda la app y tests.
 */
fun estadoActividad(actividad: ActividadFormativa): String {
    return when {
        actividad.progreso >= 100 -> "COMPLETADA"
        actividad.fechaFin.isBefore(LocalDate.now()) && actividad.progreso < 100 -> "VENCIDA"
        actividad.progreso > 0 -> "EN PROCESO"
        else -> "PENDIENTE"
    }
}
