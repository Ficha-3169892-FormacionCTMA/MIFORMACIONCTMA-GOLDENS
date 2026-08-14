package com.samuel.miformacionctma.model

enum class Prioridad {
    BAJA, MEDIA, ALTA
}

enum class EstadoActividadEnum {
    PENDIENTE, EN_PROCESO, ENTREGADA, CALIFICADA
}

data class ActividadFormativa(
    val id: Int,
    val titulo: String,
    val descripcion: String? = null,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad,
    val estado: EstadoActividadEnum = EstadoActividadEnum.PENDIENTE,
    val urlEvidencia: String? = null
)

/**
 * Función pura de negocio que calcula la etiqueta textual del estado según las reglas
 */
fun estadoActividad(actividad: ActividadFormativa): String {
    return when {
        actividad.estado == EstadoActividadEnum.ENTREGADA -> "Entregada"
        actividad.estado == EstadoActividadEnum.CALIFICADA -> "Calificada"
        actividad.progreso >= 100 -> "Entregada"
        actividad.progreso > 0 -> "En Proceso"
        else -> "Pendiente"
    }
}

/**
 * Función de negocio para validar y realizar la entrega de una evidencia
 */
fun entregarActividad(actividad: ActividadFormativa, url: String): Result<ActividadFormativa> {
    if (url.trim().isEmpty()) {
        return Result.failure(IllegalArgumentException("La URL de evidencia no puede estar vacía"))
    }
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        return Result.failure(IllegalArgumentException("La URL debe iniciar con http:// o https://"))
    }

    val actividadActualizada = actividad.copy(
        progreso = 100,
        estado = EstadoActividadEnum.ENTREGADA,
        urlEvidencia = url.trim()
    )
    return Result.success(actividadActualizada)
}