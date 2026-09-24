package com.samuel.miformacionctma.sync

import android.content.Context
import android.net.Uri
import android.util.Log
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.AsignacionActividadEntity
import com.samuel.miformacionctma.data.local.entities.AsistenciaEntity
import com.samuel.miformacionctma.data.local.entities.BitacoraEntity
import com.samuel.miformacionctma.data.local.entities.EvidenciaEntity
import com.samuel.miformacionctma.data.local.entities.NovedadEntity
import com.samuel.miformacionctma.data.repository.PerfilDto
import com.samuel.miformacionctma.network.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.hours

class SyncManager(
    private val context: Context,
    private val db: AppDatabase
) {

    suspend fun syncActividadesPush() {
        try {
            val unsynced = db.actividadDao().getUnsyncedActividades()
            for (actividad in unsynced) {
                val dto = InsertActividadSupabaseDto(
                    titulo = actividad.titulo,
                    descripcion = actividad.descripcion,
                    fechaInicio = actividad.fechaInicio.toString(),
                    fechaFin = actividad.fechaFin.toString(),
                    progreso = actividad.progreso,
                    prioridad = actividad.prioridad.name.uppercase(),
                    instructorId = actividad.instructorId
                )
                val insertedList = SupabaseProvider.client.postgrest["actividad_formativa"]
                    .insert(dto) { select() }
                    .decodeList<ActividadSupabaseDto>()

                val remoteId = insertedList.firstOrNull()?.id
                db.actividadDao().updateActividad(actividad.copy(isSynced = true, remoteId = remoteId))
            }
        } catch (e: Exception) {
            Log.e("SyncManager.ActividadesPush", "Error en syncActividadesPush: ${e.message}", e)
        }
    }

    suspend fun syncBitacorasPush() {
        val unsynced = db.bitacoraDao().getUnsyncedBitacoras()
        for (bitacora in unsynced) {
            try {
                Log.d("BitacoraDebug", "Iniciando push de bitacora id=${bitacora.id}, remoteId=${bitacora.remoteId}, titulo=${bitacora.titulo}")
                val dto = BitacoraSupabaseDto(
                    id = bitacora.remoteId,
                    autorId = bitacora.autorId,
                    fecha = bitacora.fecha.toString(),
                    titulo = bitacora.titulo,
                    contenido = bitacora.contenido,
                    horas = bitacora.horas
                )
                val insertedList = SupabaseProvider.client.postgrest["bitacora"]
                    .upsert(dto) { select() }
                    .decodeList<BitacoraSupabaseDto>()

                val remoteId = insertedList.firstOrNull()?.id ?: bitacora.remoteId
                db.bitacoraDao().updateBitacora(bitacora.copy(isSynced = true, remoteId = remoteId))
                Log.d("BitacoraDebug", "Push exitoso para bitacora id=${bitacora.id}, remoteId=$remoteId")
            } catch (e: Exception) {
                Log.e("BitacoraDebug", "Error al enviar bitacora id=${bitacora.id}, remoteId=${bitacora.remoteId}: ${e.message}", e)
            }
        }
    }

    suspend fun syncBitacorasPull() {
        try {
            val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id
            if (uid == null) {
                Log.e("SyncManager.BitacorasPull", "No hay usuario autenticado en Supabase Auth")
                return
            }

            val list = SupabaseProvider.client.postgrest["bitacora"]
                .select {
                    filter {
                        eq("autor_id", uid)
                    }
                }
                .decodeList<BitacoraSupabaseDto>()

            for (dto in list) {
                val remoteId = dto.id ?: continue
                val existing = db.bitacoraDao().getBitacoraByRemoteId(remoteId)
                if (existing != null && !existing.isSynced) {
                    Log.d("BitacoraDebug", "Saltando pull para remoteId=$remoteId pues existen cambios locales no sincronizados")
                    continue
                }
                val entity = BitacoraEntity(
                    id = existing?.id ?: 0L,
                    autorId = dto.autorId,
                    fecha = try { LocalDate.parse(dto.fecha) } catch (e: Exception) { LocalDate.now() },
                    titulo = dto.titulo,
                    contenido = dto.contenido,
                    horas = dto.horas,
                    isSynced = true,
                    remoteId = remoteId
                )
                db.bitacoraDao().insertBitacora(entity)
            }
        } catch (e: Exception) {
            Log.e("SyncManager.BitacorasPull", "Error en syncBitacorasPull: ${e.message}", e)
        }
    }

    suspend fun syncAsistenciasPush() {
        try {
            val unsynced = db.asistenciaDao().getUnsyncedAsistencia()
            for (asistencia in unsynced) {
                val dto = AsistenciaSupabaseDto(
                    aprendizId = asistencia.aprendizId,
                    fecha = asistencia.fecha.toString(),
                    estuvoPresente = asistencia.estuvoPresente,
                    observacion = asistencia.observacion
                )
                val insertedList = SupabaseProvider.client.postgrest["asistencia"]
                    .upsert(dto) { select() }
                    .decodeList<AsistenciaSupabaseDto>()

                val remoteId = insertedList.firstOrNull()?.id ?: asistencia.remoteId
                db.asistenciaDao().updateAsistencia(asistencia.copy(isSynced = true, remoteId = remoteId))
            }
        } catch (e: Exception) {
            Log.e("SyncManager.AsistenciasPush", "Error en syncAsistenciasPush: ${e.message}", e)
        }
    }

    suspend fun syncAsistenciasPull() {
        try {
            val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id
            if (uid == null) {
                Log.e("SyncManager.AsistenciasPull", "No hay usuario autenticado en Supabase Auth")
                return
            }

            val list = SupabaseProvider.client.postgrest["asistencia"]
                .select {
                    filter {
                        eq("aprendiz_id", uid)
                    }
                }
                .decodeList<AsistenciaSupabaseDto>()

            for (dto in list) {
                val remoteId = dto.id ?: continue
                val existing = db.asistenciaDao().getAsistenciaByRemoteId(remoteId)
                    ?: db.asistenciaDao().getAsistenciaByDate(dto.aprendizId, dto.fecha)
                val entity = AsistenciaEntity(
                    id = existing?.id ?: 0L,
                    aprendizId = dto.aprendizId,
                    fecha = try { LocalDate.parse(dto.fecha) } catch (e: Exception) { LocalDate.now() },
                    estuvoPresente = dto.estuvoPresente,
                    observacion = dto.observacion,
                    isSynced = true,
                    remoteId = remoteId
                )
                db.asistenciaDao().insertAsistencia(entity)
            }
        } catch (e: Exception) {
            Log.e("SyncManager.AsistenciasPull", "Error en syncAsistenciasPull: ${e.message}", e)
        }
    }

    suspend fun syncEvidenciasPush() {
        val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id
        if (uid == null) {
            Log.e("SyncManager.EvidenciasPush", "No hay un usuario autenticado en Supabase Auth")
            return
        }

        val unsynced = db.evidenciaDao().getUnsyncedEvidencias()
        for (evidencia in unsynced) {
            try {
                val actividad = db.actividadDao().getActividadById(evidencia.actividadId)
                val targetActividadId = actividad?.remoteId ?: evidencia.actividadId
                val rpcParams = buildJsonObject {
                    put("p_actividad_id", targetActividadId)
                }
                val asignacionId = SupabaseProvider.client.postgrest.rpc("obtener_asignacion", rpcParams).decodeAs<Long>()

                val dto = EvidenciaSupabaseDto(
                    asignacionId = asignacionId,
                    comentarioAprendiz = evidencia.comentarioAprendiz,
                    fechaEntrega = evidencia.fechaEntrega.toString()
                )

                val insertedList = SupabaseProvider.client.postgrest["evidencia"]
                    .insert(dto) { select() }
                    .decodeList<EvidenciaSupabaseDto>()
                
                val remoteEvidenciaId = insertedList.firstOrNull()?.id
                    ?: throw Exception("No se devolvió un ID para la evidencia insertada.")

                var storagePath = evidencia.storagePath
                var signedUrl = evidencia.url

                if (evidencia.evidenciaUri.isNotBlank()) {
                    val uri = Uri.parse(evidencia.evidenciaUri)
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: throw Exception("No se pudo leer el contenido de la URI: ${evidencia.evidenciaUri}")

                    storagePath = "$uid/$remoteEvidenciaId/${evidencia.nombreArchivo}"
                    SupabaseProvider.client.storage["evidencias"].upload(storagePath, bytes) {
                        upsert = true
                    }

                    try {
                        signedUrl = SupabaseProvider.client.storage["evidencias"].createSignedUrl(
                            path = storagePath,
                            expiresIn = 1.hours
                        )
                    } catch (e: Exception) {
                        Log.e("SyncManager.EvidenciasPush", "Error generando signedUrl: ${e.message}", e)
                    }

                    val fotoDto = EvidenciaFotoSupabaseDto(
                        evidenciaId = remoteEvidenciaId,
                        storagePath = storagePath
                    )
                    SupabaseProvider.client.postgrest["evidencia_foto"].insert(fotoDto)
                }

                db.evidenciaDao().updateEvidencia(
                    evidencia.copy(
                        isSynced = true,
                        storagePath = storagePath,
                        url = if (signedUrl.isNotBlank()) signedUrl else evidencia.url,
                        estadoSincronizacion = "SINCRONIZADA"
                    )
                )
            } catch (e: Exception) {
                Log.e("SyncManager.EvidenciasPush", "Error al sincronizar evidencia ${evidencia.id}: ${e.message}", e)
            }
        }
    }

    suspend fun syncEvidenciasPull() {
        try {
            val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id ?: return

            val asignaciones = SupabaseProvider.client.postgrest["asignacion_actividad"]
                .select {
                    filter {
                        eq("aprendiz_id", uid)
                    }
                }
                .decodeList<AsignacionActividadSupabaseDto>()

            if (asignaciones.isEmpty()) return
            val asignacionIds = asignaciones.map { it.id }
            val mapaAsignacionToActividad = asignaciones.associate { it.id to it.actividadId }

            val evidenciasRemotas = SupabaseProvider.client.postgrest["evidencia"]
                .select {
                    filter {
                        isIn("asignacion_id", asignacionIds)
                    }
                }
                .decodeList<EvidenciaSupabaseDto>()

            if (evidenciasRemotas.isEmpty()) return

            val evidenciaIds = evidenciasRemotas.mapNotNull { it.id }
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

            for (dto in evidenciasRemotas) {
                val remoteEvidenciaId = dto.id ?: continue
                val remoteActividadId = mapaAsignacionToActividad[dto.asignacionId] ?: continue

                val actividadLocal = db.actividadDao().getActividadByRemoteId(remoteActividadId)
                val localActividadId = actividadLocal?.id ?: remoteActividadId

                val foto = mapaFotos[remoteEvidenciaId]
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

                val fechaEntrega = try {
                    LocalDateTime.parse(dto.fechaEntrega.replace(" ", "T"))
                } catch (e: Exception) {
                    LocalDateTime.now()
                }

                val previasLocales = db.evidenciaDao().getEvidenciasByActividadYUsuario(localActividadId, uid)
                val idLocal = previasLocales.firstOrNull()?.id ?: 0L
                val uriLocal = previasLocales.firstOrNull()?.evidenciaUri ?: ""

                val entity = EvidenciaEntity(
                    id = idLocal,
                    actividadId = localActividadId,
                    userId = uid,
                    nombreArchivo = storagePath.substringAfterLast("/", "Evidencia_$remoteEvidenciaId.jpg"),
                    storagePath = storagePath,
                    url = signedUrl,
                    fechaEntrega = fechaEntrega,
                    comentarioAprendiz = dto.comentarioAprendiz,
                    isSynced = true,
                    evidenciaUri = uriLocal,
                    mimeType = "image/jpeg",
                    tamanoBytes = 0L,
                    estadoSincronizacion = "SINCRONIZADA"
                )

                db.evidenciaDao().insertEvidencia(entity)
            }
        } catch (e: Exception) {
            Log.e("SyncManager.EvidenciasPull", "Error en syncEvidenciasPull: ${e.message}", e)
        }
    }

    suspend fun syncAsignacionesPull() {
        try {
            val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id ?: return
            val list = SupabaseProvider.client.postgrest["asignacion_actividad"]
                .select {
                    filter {
                        eq("aprendiz_id", uid)
                    }
                }
                .decodeList<AsignacionActividadSupabaseDto>()

            val entities = list.map {
                AsignacionActividadEntity(
                    id = it.id,
                    actividadId = it.actividadId,
                    aprendizId = it.aprendizId,
                    fechaAsignacion = it.fechaAsignacion,
                    estado = it.estado
                )
            }
            db.asignacionActividadDao().insertAsignaciones(entities)
        } catch (e: Exception) {
            Log.e("SyncManager.AsignacionesPull", "Error en syncAsignacionesPull: ${e.message}", e)
        }
    }

    suspend fun syncNovedadesPush() {
        val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id
        if (uid == null) {
            Log.e("SyncManager.NovedadesPush", "No hay un usuario autenticado en Supabase Auth")
            return
        }

        try {
            val unsynced = db.novedadDao().getUnsyncedNovedades()
            for (novedad in unsynced) {
                if (novedad.userId != uid) {
                    Log.w("SyncManager.NovedadesPush", "Omitiendo novedad id=${novedad.id} pues pertenece a un usuario distinto (${novedad.userId}) al activo ($uid)")
                    continue
                }

                val dto = InsertNovedadSupabaseDto(
                    autorId = novedad.userId,
                    tipoAutor = novedad.tipoAutor,
                    tipo = novedad.tipo,
                    fechaInicio = novedad.fecha.toString(),
                    fechaFin = novedad.fechaFin?.toString(),
                    motivo = novedad.motivo,
                    documentoAdjunto = novedad.documentoAdjunto
                )
                Log.d("DEBUG_NOVEDAD_PUSH", "novedad.userId=\"${novedad.userId}\", uid=\"$uid\", tipoAutor=\"${novedad.tipoAutor}\", dto=$dto")
                val insertedList = SupabaseProvider.client.postgrest["novedades"]
                    .insert(dto) { select() }
                    .decodeList<NovedadSupabaseDto>()

                val remoteId = insertedList.firstOrNull()?.id ?: novedad.remoteId
                db.novedadDao().updateNovedad(novedad.copy(isSynced = true, remoteId = remoteId))
            }
        } catch (e: Exception) {
            Log.e("SyncManager.NovedadesPush", "Error en syncNovedadesPush: ${e.message}", e)
        }
    }

    suspend fun syncNovedadesPull() {
        try {
            val uid = SupabaseProvider.client.auth.currentUserOrNull()?.id ?: return

            val list = SupabaseProvider.client.postgrest["novedades"]
                .select()
                .decodeList<NovedadSupabaseDto>()

            if (list.isEmpty()) return

            val autorIds = list.map { it.autorId }.distinct()
            val perfiles = if (autorIds.isNotEmpty()) {
                try {
                    SupabaseProvider.client.postgrest["perfiles"]
                        .select {
                            filter {
                                isIn("id", autorIds)
                            }
                        }
                        .decodeList<PerfilDto>()
                } catch (e: Exception) {
                    emptyList()
                }
            } else emptyList()

            val mapaPerfiles = perfiles.associateBy { it.id }

            for (dto in list) {
                val remoteId = dto.id ?: continue
                val existing = db.novedadDao().getNovedadByRemoteId(remoteId)
                if (existing != null && !existing.isSynced) continue

                val fechaInicio = try { LocalDate.parse(dto.fechaInicio) } catch (e: Exception) { LocalDate.now() }
                val fechaFin = try { dto.fechaFin?.let { LocalDate.parse(it) } } catch (e: Exception) { null }

                val perfil = mapaPerfiles[dto.autorId]
                val autorCorreo = perfil?.correo ?: perfil?.nombre ?: dto.autorId

                val entity = NovedadEntity(
                    id = existing?.id ?: 0L,
                    userId = dto.autorId,
                    tipo = dto.tipo,
                    motivo = dto.motivo,
                    fecha = fechaInicio,
                    fechaFin = fechaFin,
                    documentoAdjunto = dto.documentoAdjunto,
                    estado = existing?.estado ?: "PENDIENTE",
                    isSynced = true,
                    remoteId = remoteId,
                    tipoAutor = dto.tipoAutor,
                    autorCorreo = autorCorreo
                )
                db.novedadDao().insertNovedad(entity)
            }
        } catch (e: Exception) {
            Log.e("SyncManager.NovedadesPull", "Error en syncNovedadesPull: ${e.message}", e)
        }
    }

    suspend fun syncAll() {
        try { syncActividadesPush() } catch (e: Exception) { Log.e("SyncManager", "Error en syncActividadesPush: ${e.message}", e) }
        try { syncBitacorasPush() } catch (e: Exception) { Log.e("SyncManager", "Error en syncBitacorasPush: ${e.message}", e) }
        try { syncBitacorasPull() } catch (e: Exception) { Log.e("SyncManager", "Error en syncBitacorasPull: ${e.message}", e) }
        try { syncAsistenciasPush() } catch (e: Exception) { Log.e("SyncManager", "Error en syncAsistenciasPush: ${e.message}", e) }
        try { syncAsistenciasPull() } catch (e: Exception) { Log.e("SyncManager", "Error en syncAsistenciasPull: ${e.message}", e) }
        try { syncEvidenciasPush() } catch (e: Exception) { Log.e("SyncManager", "Error en syncEvidenciasPush: ${e.message}", e) }
        try { syncEvidenciasPull() } catch (e: Exception) { Log.e("SyncManager", "Error en syncEvidenciasPull: ${e.message}", e) }
        try { syncAsignacionesPull() } catch (e: Exception) { Log.e("SyncManager", "Error en syncAsignacionesPull: ${e.message}", e) }
        try { syncNovedadesPush() } catch (e: Exception) { Log.e("SyncManager", "Error en syncNovedadesPush: ${e.message}", e) }
        try { syncNovedadesPull() } catch (e: Exception) { Log.e("SyncManager", "Error en syncNovedadesPull: ${e.message}", e) }
    }
}
