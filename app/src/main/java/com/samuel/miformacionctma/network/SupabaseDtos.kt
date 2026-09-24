package com.samuel.miformacionctma.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BitacoraSupabaseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("autor_id") val autorId: String,
    @SerialName("fecha") val fecha: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("contenido") val contenido: String,
    @SerialName("horas") val horas: Int
)

@Serializable
data class AsistenciaSupabaseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("aprendiz_id") val aprendizId: String,
    @SerialName("fecha") val fecha: String,
    @SerialName("estuvo_presente") val estuvoPresente: Boolean,
    @SerialName("observacion") val observacion: String?
)

@Serializable
data class EvidenciaSupabaseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("asignacion_id") val asignacionId: Long,
    @SerialName("comentario_aprendiz") val comentarioAprendiz: String?,
    @SerialName("fecha_entrega") val fechaEntrega: String
)

@Serializable
data class EvidenciaFotoSupabaseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("evidencia_id") val evidenciaId: Long,
    @SerialName("storage_path") val storagePath: String
)

@Serializable
data class AsignacionActividadSupabaseDto(
    @SerialName("id") val id: Long,
    @SerialName("actividad_id") val actividadId: Long,
    @SerialName("aprendiz_id") val aprendizId: String,
    @SerialName("fecha_asignacion") val fechaAsignacion: String,
    @SerialName("estado") val estado: String = "pendiente"
)

@Serializable
data class ActividadSupabaseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String,
    @SerialName("progreso") val progreso: Int = 0,
    @SerialName("prioridad") val prioridad: String,
    @SerialName("instructor_id") val instructorId: String
)

@Serializable
data class InsertActividadSupabaseDto(
    @SerialName("titulo") val titulo: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String,
    @SerialName("progreso") val progreso: Int = 0,
    @SerialName("prioridad") val prioridad: String,
    @SerialName("instructor_id") val instructorId: String
)

@Serializable
data class NovedadSupabaseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("autor_id") val autorId: String,
    @SerialName("tipo_autor") val tipoAutor: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String? = null,
    @SerialName("motivo") val motivo: String,
    @SerialName("documento_adjunto") val documentoAdjunto: String? = null
)

@Serializable
data class InsertNovedadSupabaseDto(
    @SerialName("autor_id") val autorId: String,
    @SerialName("tipo_autor") val tipoAutor: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String? = null,
    @SerialName("motivo") val motivo: String,
    @SerialName("documento_adjunto") val documentoAdjunto: String? = null
)
