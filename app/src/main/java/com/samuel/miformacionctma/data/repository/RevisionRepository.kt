package com.samuel.miformacionctma.data.repository

import android.util.Log
import com.samuel.miformacionctma.network.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.hours

data class EntregaParaRevision(
    val asignacionId: Long,
    val aprendizNombre: String,
    val aprendizCorreo: String,
    val estado: String,
    val comentario: String?,
    val fechaEntrega: String?,
    val fotoUrl: String? = null
)

data class DesempenoAprendiz(
    val nombre: String,
    val correo: String,
    val completadas: Int,
    val totalAsignadas: Int,
    val promedio: Int
)

data class InstructorStats(
    val porCalificar: Int,
    val aprendices: List<DesempenoAprendiz>
)

class RevisionRepository {

    suspend fun getEntregasParaActividad(actividadRemoteId: Long): Result<List<EntregaParaRevision>> = withContext(Dispatchers.IO) {
        try {
            val asignaciones = SupabaseProvider.client.postgrest["asignacion_actividad"]
                .select {
                    filter {
                        eq("actividad_id", actividadRemoteId)
                    }
                }
                .decodeList<AsignacionActividadSupabaseDto>()

            if (asignaciones.isEmpty()) {
                return@withContext Result.success(emptyList())
            }

            val aprendizIds = asignaciones.map { it.aprendizId }.distinct()
            val asignacionIds = asignaciones.map { it.id }

            val perfiles = SupabaseProvider.client.postgrest["perfiles"]
                .select {
                    filter {
                        isIn("id", aprendizIds)
                    }
                }
                .decodeList<PerfilDto>()
            val mapaPerfiles = perfiles.associateBy { it.id }

            val evidencias = SupabaseProvider.client.postgrest["evidencia"]
                .select {
                    filter {
                        isIn("asignacion_id", asignacionIds)
                    }
                }
                .decodeList<EvidenciaSupabaseDto>()
            val mapaEvidencias = evidencias.associateBy { it.asignacionId }

            val evidenciaIds = evidencias.mapNotNull { it.id }
            val fotos = if (evidenciaIds.isNotEmpty()) {
                SupabaseProvider.client.postgrest["evidencia_foto"]
                    .select {
                        filter {
                            isIn("evidencia_id", evidenciaIds)
                        }
                    }
                    .decodeList<EvidenciaFotoSupabaseDto>()
            } else emptyList()
            val mapaFotos = fotos.associateBy { it.evidenciaId }

            val entregas = asignaciones.map { asig ->
                val perfil = mapaPerfiles[asig.aprendizId]
                val evidencia = mapaEvidencias[asig.id]
                val foto = evidencia?.id?.let { mapaFotos[it] }
                val storagePath = foto?.storagePath ?: ""
                val signedUrl = if (storagePath.isNotBlank()) {
                    try {
                        SupabaseProvider.client.storage["evidencias"].createSignedUrl(
                            path = storagePath,
                            expiresIn = 1.hours
                        )
                    } catch (e: Exception) {
                        ""
                    }
                } else ""

                EntregaParaRevision(
                    asignacionId = asig.id,
                    aprendizNombre = perfil?.nombre ?: "Aprendiz SENA",
                    aprendizCorreo = perfil?.correo ?: asig.aprendizId,
                    estado = asig.estado,
                    comentario = evidencia?.comentarioAprendiz,
                    fechaEntrega = evidencia?.fechaEntrega,
                    fotoUrl = if (signedUrl.isNotBlank()) signedUrl else null
                )
            }

            Result.success(entregas)
        } catch (e: Exception) {
            Log.e("RevisionRepository", "Error al obtener entregas: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun actualizarEstadoAsignacion(asignacionId: Long, nuevoEstado: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseProvider.client.postgrest["asignacion_actividad"]
                .update({
                    set("estado", nuevoEstado)
                }) {
                    filter {
                        eq("id", asignacionId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RevisionRepository", "Error al actualizar estado asignacion $asignacionId: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getConteosEnRevisionPorInstructor(instructorId: String): Result<Map<Long, Int>> = withContext(Dispatchers.IO) {
        try {
            val actividades = SupabaseProvider.client.postgrest["actividad_formativa"]
                .select {
                    filter {
                        eq("instructor_id", instructorId)
                    }
                }
                .decodeList<ActividadSupabaseDto>()

            if (actividades.isEmpty()) {
                return@withContext Result.success(emptyMap())
            }

            val actIds = actividades.mapNotNull { it.id }

            val asignaciones = SupabaseProvider.client.postgrest["asignacion_actividad"]
                .select {
                    filter {
                        isIn("actividad_id", actIds)
                        eq("estado", "en_revision")
                    }
                }
                .decodeList<AsignacionActividadSupabaseDto>()

            val map = asignaciones.groupBy { it.actividadId }.mapValues { it.value.size }
            Result.success(map)
        } catch (e: Exception) {
            Log.e("RevisionRepository", "Error al obtener conteos en revisión: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getEstadisticasInstructor(instructorId: String): Result<InstructorStats> = withContext(Dispatchers.IO) {
        try {
            val actividades = SupabaseProvider.client.postgrest["actividad_formativa"]
                .select {
                    filter {
                        eq("instructor_id", instructorId)
                    }
                }
                .decodeList<ActividadSupabaseDto>()

            if (actividades.isEmpty()) {
                return@withContext Result.success(InstructorStats(0, emptyList()))
            }

            val actIds = actividades.mapNotNull { it.id }

            val asignaciones = SupabaseProvider.client.postgrest["asignacion_actividad"]
                .select {
                    filter {
                        isIn("actividad_id", actIds)
                    }
                }
                .decodeList<AsignacionActividadSupabaseDto>()

            val totalActividadesInstructor = actIds.size
            val porCalificar = asignaciones.count { it.estado == "en_revision" }

            val perfilesAprendices = try {
                SupabaseProvider.client.postgrest["perfiles"]
                    .select {
                        filter {
                            eq("rol", "aprendiz")
                        }
                    }
                    .decodeList<PerfilDto>()
            } catch (e: Exception) {
                emptyList()
            }

            val aprendizIdsConAsignacion = asignaciones.map { it.aprendizId }.distinct()
            val todosAprendizIds = (perfilesAprendices.map { it.id } + aprendizIdsConAsignacion).distinct()
            val mapaPerfiles = perfilesAprendices.associateBy { it.id }

            val listaAprendices = todosAprendizIds.map { aprendizId ->
                val listaAsig = asignaciones.filter { it.aprendizId == aprendizId }
                val total = if (totalActividadesInstructor > 0) totalActividadesInstructor else listaAsig.size
                val completadas = listaAsig.count { it.estado == "completada" }
                val promedio = if (total > 0) ((completadas.toDouble() / total) * 100).toInt() else 0
                val perfil = mapaPerfiles[aprendizId]

                DesempenoAprendiz(
                    nombre = perfil?.nombre ?: "Aprendiz SENA",
                    correo = perfil?.correo ?: aprendizId,
                    completadas = completadas,
                    totalAsignadas = total,
                    promedio = promedio
                )
            }.sortedByDescending { it.promedio }

            Result.success(InstructorStats(porCalificar, listaAprendices))
        } catch (e: Exception) {
            Log.e("RevisionRepository", "Error al obtener estadísticas del instructor: ${e.message}", e)
            Result.failure(e)
        }
    }
}
