package com.samuel.miformacionctma.sync

import android.content.Context
import android.net.Uri
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.network.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class SyncManager(
    private val context: Context,
    private val db: AppDatabase
) {
    private val supabaseClient = SupabaseProvider.client

    suspend fun syncEverything() = withContext(Dispatchers.IO) {
        syncActividadesPush()
        syncAsistenciasPush()
        syncBitacorasPush()
        syncEvidenciasPush()
        syncActividadesPull()
    }

    private suspend fun syncActividadesPush() {
        val pending = db.actividadDao().getUnsyncedActividades()
        for (actividad in pending) {
            try {
                val dto = ActividadSupabaseDto(
                    id = actividad.id,
                    titulo = actividad.titulo,
                    descripcion = actividad.descripcion,
                    fecha_inicio = actividad.fechaInicio.toString(),
                    fecha_fin = actividad.fechaFin.toString(),
                    progreso = actividad.progreso,
                    prioridad = actividad.prioridad.name,
                    instructor_id = actividad.instructorId
                )
                supabaseClient.postgrest["actividades"].upsert(dto)
                db.actividadDao().updateActividad(actividad.copy(syncStatus = "SINCRONIZADO"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun syncAsistenciasPush() {
        val pending = db.asistenciaDao().getUnsyncedAsistencia()
        for (asistencia in pending) {
            try {
                val dto = AsistenciaSupabaseDto(
                    id = if (asistencia.remoteId == 0L) null else asistencia.remoteId,
                    user_id = asistencia.userId,
                    fecha = asistencia.fecha.toString(),
                    estuvo_presente = asistencia.estuvoPresente,
                    observacion = asistencia.observacion
                )
                val response = supabaseClient.postgrest["asistencia"].insert(dto) {
                    select()
                }
                val remoteRecord = response.decodeSingle<AsistenciaSupabaseDto>()
                db.asistenciaDao().updateAsistencia(
                    asistencia.copy(
                        remoteId = remoteRecord.id,
                        syncStatus = "SINCRONIZADO"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun syncBitacorasPush() {
        val pending = db.bitacoraDao().getUnsyncedBitacoras()
        for (bitacora in pending) {
            try {
                val dto = BitacoraSupabaseDto(
                    id = if (bitacora.remoteId == 0L) null else bitacora.remoteId,
                    user_id = bitacora.userId,
                    fecha = bitacora.fecha.toString(),
                    titulo = bitacora.titulo,
                    contenido = bitacora.contenido,
                    horas = bitacora.horas
                )
                val response = supabaseClient.postgrest["bitacoras"].insert(dto) {
                    select()
                }
                val remoteRecord = response.decodeSingle<BitacoraSupabaseDto>()
                db.bitacoraDao().updateBitacora(
                    bitacora.copy(
                        remoteId = remoteRecord.id,
                        syncStatus = "SINCRONIZADO"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun syncEvidenciasPush() {
        val pending = db.evidenciaDao().getUnsyncedEvidencias()
        for (evidencia in pending) {
            try {
                var remoteUrl = evidencia.url
                if (evidencia.evidenciaUri.isNotEmpty() && remoteUrl.isEmpty()) {
                    val uri = Uri.parse(evidencia.evidenciaUri)
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        val bytes = stream.readBytes()
                        val bucket = supabaseClient.storage["evidencias"]
                        val path = "${evidencia.userId}/${evidencia.actividadId}/${evidencia.nombreArchivo}"
                        bucket.upload(path, bytes) {
                            upsert = true
                        }
                        remoteUrl = bucket.publicUrl(path)
                    }
                }

                val dto = EvidenciaSupabaseDto(
                    id = if (evidencia.remoteId == 0L) null else evidencia.remoteId,
                    actividad_id = evidencia.actividadId,
                    user_id = evidencia.userId,
                    nombre_archivo = evidencia.nombreArchivo,
                    url = remoteUrl,
                    fecha_entrega = evidencia.fechaEntrega.toString(),
                    comentario_aprendiz = evidencia.comentarioAprendiz
                )
                val response = supabaseClient.postgrest["evidencias"].insert(dto) {
                    select()
                }
                val remoteRecord = response.decodeSingle<EvidenciaSupabaseDto>()
                db.evidenciaDao().updateEvidencia(
                    evidencia.copy(
                        remoteId = remoteRecord.id,
                        url = remoteUrl,
                        syncStatus = "SINCRONIZADO"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun syncActividadesPull() {
        try {
            val response = supabaseClient.postgrest["actividades"].select()
            val remoteList = response.decodeList<ActividadSupabaseDto>()
            for (dto in remoteList) {
                val local = db.actividadDao().getActividadById(dto.id)
                if (local == null || local.syncStatus == "SINCRONIZADO") {
                    val entity = ActividadEntity(
                        id = dto.id,
                        titulo = dto.titulo,
                        descripcion = dto.descripcion,
                        fechaInicio = java.time.LocalDate.parse(dto.fecha_inicio),
                        fechaFin = java.time.LocalDate.parse(dto.fecha_fin),
                        progreso = dto.progreso,
                        prioridad = com.samuel.miformacionctma.model.Prioridad.valueOf(dto.prioridad),
                        instructorId = dto.instructor_id,
                        syncStatus = "SINCRONIZADO"
                    )
                    db.actividadDao().insertActividad(entity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
