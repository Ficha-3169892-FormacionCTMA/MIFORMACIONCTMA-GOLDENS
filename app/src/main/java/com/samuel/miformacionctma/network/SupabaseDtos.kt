package com.samuel.miformacionctma.network

import kotlinx.serialization.Serializable

@Serializable
data class ActividadSupabaseDto(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val fecha_inicio: String,
    val fecha_fin: String,
    val progreso: Int,
    val prioridad: String,
    val instructor_id: String
)

@Serializable
data class AsistenciaSupabaseDto(
    val id: Long? = null,
    val user_id: String,
    val fecha: String,
    val estuvo_presente: Boolean,
    val observacion: String?
)

@Serializable
data class BitacoraSupabaseDto(
    val id: Long? = null,
    val user_id: String,
    val fecha: String,
    val titulo: String,
    val contenido: String,
    val horas: Int
)

@Serializable
data class EvidenciaSupabaseDto(
    val id: Long? = null,
    val actividad_id: Long,
    val user_id: String,
    val nombre_archivo: String,
    val url: String,
    val fecha_entrega: String,
    val comentario_aprendiz: String?
)
