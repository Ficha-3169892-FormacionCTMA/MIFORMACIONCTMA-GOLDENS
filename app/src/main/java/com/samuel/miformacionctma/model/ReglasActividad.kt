package com.samuel.miformacionctma.model

/**
 * Calcula el estado de una actividad según su progreso y días restantes.
 */
fun estadoActividad(actividad: ActividadFormativa): String {
    return when {
        actividad.progreso >= 100 -> "Completada"
        actividad.diasRestantes < 0 -> "Vencida"
        actividad.progreso > 0 -> "En progreso"
        else -> "Pendiente"
    }
}

/**
 * Filtra las actividades que faltan por entregar o tienen pocos días restantes.
 */
fun actividadesUrgentes(lista: List<ActividadFormativa>): List<ActividadFormativa> {
    return lista.filter { it.diasRestantes in 0..3 && it.progreso < 100 }
}

/**
 * Calcula el promedio general de progreso de una lista de actividades.
 */
fun promedioProgreso(lista: List<ActividadFormativa>): Float {
    if (lista.isEmpty()) return 0f
    val suma = lista.sumOf { it.progreso }
    return suma.toFloat() / lista.size
}