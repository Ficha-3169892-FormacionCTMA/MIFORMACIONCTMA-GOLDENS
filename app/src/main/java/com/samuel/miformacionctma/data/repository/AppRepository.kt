package com.samuel.miformacionctma.data.repository

import android.util.Log
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.network.EvidenciaSupabaseDto
import com.samuel.miformacionctma.network.NetworkResult
import com.samuel.miformacionctma.network.NetworkError
import com.samuel.miformacionctma.network.RemoteActividadDataSource
import com.samuel.miformacionctma.network.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.hours

/**
 * Repositorio base marcado como 'open' para permitir la creación de Fakes en tests unitarios.
 */
open class AppRepository(
    private val db: AppDatabase,
    private val remoteDataSource: RemoteActividadDataSource
) {

    private fun ActividadEntity.toDomain() = ActividadFormativa(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        progreso = progreso,
        diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaFin).toInt(),
        prioridad = prioridad,
        instructorId = instructorId,
        remoteId = remoteId
    )

    private fun ActividadFormativa.toEntity(instructorId: String = this.instructorId, isSynced: Boolean = true) = ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        progreso = progreso,
        prioridad = prioridad,
        instructorId = instructorId,
        isSynced = isSynced
    )

    open fun getActividadesStream(query: String = ""): Flow<List<ActividadFormativa>> {
        val flow = if (query.isEmpty()) {
            db.actividadDao().getAllActividades()
        } else {
            db.actividadDao().searchActividades(query)
        }
        return flow.map { list -> list.map { it.toDomain() } }
    }
    
    open suspend fun refreshActividades(): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        when (val result = remoteDataSource.getActividades()) {
            is NetworkResult.Success -> {
                for (dto in result.data) {
                    val remoteId = dto.id ?: continue
                    val existing = db.actividadDao().getActividadByRemoteId(remoteId)
                    val entity = ActividadEntity(
                        id = existing?.id ?: 0L,
                        titulo = dto.titulo,
                        descripcion = dto.descripcion,
                        fechaInicio = try { LocalDate.parse(dto.fechaInicio) } catch (e: Exception) { LocalDate.now() },
                        fechaFin = try { LocalDate.parse(dto.fechaFin) } catch (e: Exception) { LocalDate.now() },
                        progreso = dto.progreso,
                        prioridad = try { Prioridad.valueOf(dto.prioridad.uppercase()) } catch (e: Exception) { Prioridad.MEDIA },
                        instructorId = dto.instructorId,
                        isSynced = true,
                        remoteId = remoteId
                    )
                    db.actividadDao().insertActividad(entity)
                }
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
        }
    }

    open suspend fun getActividadById(id: Long): ActividadFormativa? = withContext(Dispatchers.IO) {
        db.actividadDao().getActividadById(id)?.toDomain()
    }

    open suspend fun getActividadEntityById(id: Long): ActividadEntity? = withContext(Dispatchers.IO) {
        db.actividadDao().getActividadById(id)
    }
    
    open suspend fun saveActividad(actividad: ActividadFormativa, instructorId: String) = withContext(Dispatchers.IO) {
        db.actividadDao().insertActividad(actividad.toEntity(instructorId = instructorId, isSynced = false))
    }

    open suspend fun deleteActividad(actividad: ActividadEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (actividad.remoteId != null) {
                SupabaseProvider.client.postgrest["actividad_formativa"].delete {
                    filter {
                        eq("id", actividad.remoteId)
                    }
                }
            }
            db.actividadDao().deleteActividadById(actividad.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepository.DeleteActividad", "Error al borrar la actividad: ${e.message}", e)
            Result.failure(e)
        }
    }

    open suspend fun deleteActividad(actividad: ActividadFormativa) = withContext(Dispatchers.IO) {
        db.actividadDao().deleteActividad(actividad.toEntity(instructorId = "unknown"))
    }

    open fun getBitacoras(autorId: String): Flow<List<BitacoraEntity>> = db.bitacoraDao().getBitacorasByAutor(autorId)
    open suspend fun saveBitacora(bitacora: BitacoraEntity) = withContext(Dispatchers.IO) {
        db.bitacoraDao().insertBitacora(bitacora)
    }
    open suspend fun updateBitacora(bitacora: BitacoraEntity) = withContext(Dispatchers.IO) {
        db.bitacoraDao().updateBitacora(bitacora.copy(isSynced = false))
    }

    open fun getEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> = db.evidenciaDao().getEvidenciasByActividad(actividadId)
    
    open suspend fun saveEvidencia(evidencia: EvidenciaEntity) = withContext(Dispatchers.IO) {
        db.evidenciaDao().insertEvidencia(evidencia)
    }

    open suspend fun guardarEvidenciaLocal(
        actividadId: Long, 
        userId: String, 
        nombre: String, 
        uriString: String, 
        mime: String, 
        tamano: Long,
        comentario: String? = null
    ): Long = withContext(Dispatchers.IO) {
        val nuevaEvidencia = EvidenciaEntity(
            actividadId = actividadId,
            userId = userId,
            nombreArchivo = nombre,
            url = "",
            fechaEntrega = LocalDateTime.now(),
            comentarioAprendiz = comentario,
            isSynced = false,
            evidenciaUri = uriString,
            mimeType = mime,
            tamanoBytes = tamano,
            estadoSincronizacion = "LOCAL"
        )
        db.evidenciaDao().insertEvidencia(nuevaEvidencia)
        
        nuevaEvidencia.id
    }

    open suspend fun eliminarEvidenciaAnteriorYGuardarNueva(
        actividadId: Long,
        userId: String,
        nombre: String,
        uriString: String,
        mime: String,
        tamano: Long,
        comentario: String? = null
    ): Long = withContext(Dispatchers.IO) {
        try {
            val actividad = db.actividadDao().getActividadById(actividadId)
            val targetActividadId = actividad?.remoteId ?: actividadId
            val rpcParams = buildJsonObject {
                put("p_actividad_id", targetActividadId)
            }
            val asignacionId = SupabaseProvider.client.postgrest.rpc("obtener_asignacion", rpcParams).decodeAs<Long>()

            val evidenciasRemotas = SupabaseProvider.client.postgrest["evidencia"]
                .select {
                    filter {
                        eq("asignacion_id", asignacionId)
                    }
                }
                .decodeList<EvidenciaSupabaseDto>()

            for (evRemota in evidenciasRemotas) {
                val remotaId = evRemota.id ?: continue

                try {
                    SupabaseProvider.client.postgrest["evidencia_foto"].delete {
                        filter {
                            eq("evidencia_id", remotaId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AppRepository", "Error al borrar evidencia_foto remota $remotaId: ${e.message}", e)
                }

                try {
                    SupabaseProvider.client.postgrest["evidencia"].delete {
                        filter {
                            eq("id", remotaId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AppRepository", "Error al borrar evidencia remota $remotaId: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Error al consultar o borrar evidencias remotas anteriores: ${e.message}", e)
        }

        try {
            db.evidenciaDao().deleteEvidenciasByActividadYUsuario(actividadId, userId)
        } catch (e: Exception) {
            Log.e("AppRepository", "Error al borrar evidencias locales anteriores: ${e.message}", e)
        }

        guardarEvidenciaLocal(actividadId, userId, nombre, uriString, mime, tamano, comentario)
    }

    open suspend fun actualizarComentarioEvidencia(evidenciaId: Long, actividadId: Long, userId: String, nuevoComentario: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val evidenciasLocales = db.evidenciaDao().getEvidenciasByActividadYUsuario(actividadId, userId)
            val evidencia = evidenciasLocales.find { it.id == evidenciaId } ?: evidenciasLocales.firstOrNull()
            if (evidencia != null) {
                db.evidenciaDao().updateEvidencia(evidencia.copy(comentarioAprendiz = nuevoComentario, isSynced = false))
            }

            val actividad = db.actividadDao().getActividadById(actividadId)
            val targetActividadId = actividad?.remoteId ?: actividadId
            val rpcParams = buildJsonObject { put("p_actividad_id", targetActividadId) }
            val asignacionId = SupabaseProvider.client.postgrest.rpc("obtener_asignacion", rpcParams).decodeAs<Long>()

            SupabaseProvider.client.postgrest["evidencia"].update({
                set("comentario_aprendiz", nuevoComentario)
            }) {
                filter {
                    eq("asignacion_id", asignacionId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepository", "Error al actualizar comentario de evidencia: ${e.message}", e)
            Result.failure(e)
        }
    }

    open suspend fun getSignedUrlForEvidencia(storagePath: String): String = withContext(Dispatchers.IO) {
        if (storagePath.isBlank()) return@withContext ""
        try {
            SupabaseProvider.client.storage["evidencias"].createSignedUrl(
                path = storagePath,
                expiresIn = 1.hours
            )
        } catch (e: Exception) {
            Log.e("AppRepository", "Error al generar signed URL para $storagePath: ${e.message}", e)
            ""
        }
    }

    open suspend fun sincronizarEvidenciaConServidor(evidenciaId: Long): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val urlRemotaGenerada = "https://api.miformacionctma.com/storage/evidencias/evid_$evidenciaId.jpg"
            NetworkResult.Success(urlRemotaGenerada)
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError.SinConexion, e.message)
        }
    }

    open suspend fun actualizarEstadoSincronizacion(evidenciaId: Long, estado: String, urlRemota: String = "") = withContext(Dispatchers.IO) {
    }

    open fun getAsistencias(aprendizId: String): Flow<List<AsistenciaEntity>> = db.asistenciaDao().getAsistenciaByAprendiz(aprendizId)
    open suspend fun registrarAsistencia(aprendizId: String, estuvopresente: Boolean, observacion: String?) = withContext(Dispatchers.IO) {
        val hoy = LocalDate.now()
        val existe = db.asistenciaDao().getAsistenciaByDate(aprendizId, hoy.toString())
        if (existe == null) {
            db.asistenciaDao().insertAsistencia(
                AsistenciaEntity(aprendizId = aprendizId, fecha = hoy, estuvoPresente = estuvopresente, observacion = observacion)
            )
        }
    }

    open fun getNovedades(userId: String): Flow<List<NovedadEntity>> = db.novedadDao().getNovedadesByUser(userId)
    open fun getNovedadesRecibidas(currentUserId: String): Flow<List<NovedadEntity>> = db.novedadDao().getNovedadesRecibidas(currentUserId)
    open suspend fun saveNovedad(novedad: NovedadEntity) = withContext(Dispatchers.IO) {
        db.novedadDao().insertNovedad(novedad)
    }

    open fun getCertificados(userId: String): Flow<List<CertificadoEntity>> = db.certificadoDao().getCertificadosByUser(userId)

    open suspend fun insertAsignaciones(lista: List<AsignacionActividadEntity>) = withContext(Dispatchers.IO) {
        db.asignacionActividadDao().insertAsignaciones(lista)
    }

    open suspend fun getAsignacionId(actividadId: Long, aprendizId: String): Long? = withContext(Dispatchers.IO) {
        db.asignacionActividadDao().getAsignacionId(actividadId, aprendizId)
    }

    open fun getAsignacionesByAprendiz(aprendizId: String): Flow<List<AsignacionActividadEntity>> =
        db.asignacionActividadDao().getAsignacionesByAprendiz(aprendizId)
}
