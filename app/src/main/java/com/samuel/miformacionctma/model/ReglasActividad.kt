package com.samuel.miformacionctma.model

/**
 * Calcula el estado textual de una actividad basado únicamente en el progreso.
 */
fun estadoActividad(actividad: ActividadFormativa): String {
    return when {
        actividad.progreso >= 100 -> "Completada"
        actividad.progreso > 0 -> "En Proceso"
        else -> "Pendiente"
    }
}
