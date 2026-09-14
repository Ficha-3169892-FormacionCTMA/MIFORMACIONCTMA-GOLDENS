package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.model.*
import com.samuel.miformacionctma.network.NetworkResult
import com.samuel.miformacionctma.network.NetworkError
import com.samuel.miformacionctma.network.RemoteActividadDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
        syncStatus = try { SyncStatus.valueOf(syncStatus) } catch(e: Exception) { SyncStatus.SINCRONIZADO }
    )

    private fun ActividadFormativa.toEntity(instructorId: String = "default_user") = ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        progreso = progreso,
        prioridad = prioridad,
        instructorId = instructorId,
        syncStatus = syncStatus.name
    )

    private fun AsistenciaEntity.toDomain() = Asistencia(
        id = id,
        fecha = fecha,
        estuvoPresente = estuvoPresente,
        observacion = observacion,
        syncStatus = try { SyncStatus.valueOf(syncStatus) } catch(e: Exception) { SyncStatus.SINCRONIZADO }
    )

    private fun BitacoraEntity.toDomain() = Bitacora(
        id = id,
        fecha = fecha,
        titulo = titulo,
        contenido = contenido,
        horas = horas,
        syncStatus = try { SyncStatus.valueOf(syncStatus) } catch(e: Exception) { SyncStatus.SINCRONIZADO }
    )

    private fun EvidenciaEntity.toDomain() = Evidencia(
        id = id,
        actividadId = actividadId,
        nombreArchivo = nombreArchivo,
        url = url,
        fechaEntrega = fechaEntrega,
        comentarioAprendiz = comentarioAprendiz,
        syncStatus = try { SyncStatus.valueOf(syncStatus) } catch(e: Exception) { SyncStatus.SINCRONIZADO },
        evidenciaUri = evidenciaUri,
        mimeType = mimeType,
        tamanoBytes = tamanoBytes
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
                val entities = result.data.map { it.toEntity() }
                db.actividadDao().insertActividades(entities)
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
        }
    }

    open suspend fun getActividadById(id: Long): ActividadFormativa? = withContext(Dispatchers.IO) {
        db.actividadDao().getActividadById(id)?.toDomain()
    }
    
    open suspend fun saveActividad(actividad: ActividadFormativa) = withContext(Dispatchers.IO) {
        val entidadConSync = actividad.toEntity().copy(syncStatus = SyncStatus.PENDIENTE_CREAR.name)
        db.actividadDao().insertActividad(entidadConSync)
    }

    open suspend fun deleteActividad(actividad: ActividadFormativa) = withContext(Dispatchers.IO) {
        val entidadConSync = actividad.toEntity().copy(syncStatus = SyncStatus.PENDIENTE_ELIMINAR.name)
        db.actividadDao().updateActividad(entidadConSync)
    }

    open fun getBitacoras(userId: String): Flow<List<Bitacora>> = 
        db.bitacoraDao().getBitacorasByUser(userId).map { list -> list.map { it.toDomain() } }
    
    open suspend fun saveBitacora(bitacora: BitacoraEntity) = withContext(Dispatchers.IO) {
        val bitacoraConSync = bitacora.copy(syncStatus = SyncStatus.PENDIENTE_CREAR.name)
        db.bitacoraDao().insertBitacora(bitacoraConSync)
    }

    open fun getEvidencias(actividadId: Long): Flow<List<Evidencia>> = 
        db.evidenciaDao().getEvidenciasByActividad(actividadId).map { list -> list.map { it.toDomain() } }
    
    open suspend fun saveEvidencia(evidencia: EvidenciaEntity) = withContext(Dispatchers.IO) {
        val evidenciaConSync = evidencia.copy(syncStatus = SyncStatus.PENDIENTE_CREAR.name)
        db.evidenciaDao().insertEvidencia(evidenciaConSync)
        val actividad = db.actividadDao().getActividadById(evidencia.actividadId)
        actividad?.let {
            db.actividadDao().updateActividad(it.copy(progreso = 100, syncStatus = SyncStatus.PENDIENTE_ACTUALIZAR.name))
        }
    }

    open suspend fun guardarEvidenciaLocal(
        actividadId: Long, 
        userId: String, 
        nombre: String, 
        uriString: String, 
        mime: String, 
        tamano: Long
    ): Long = withContext(Dispatchers.IO) {
        val nuevaEvidencia = EvidenciaEntity(
            actividadId = actividadId,
            userId = userId,
            nombreArchivo = nombre,
            url = "",
            fechaEntrega = LocalDateTime.now(),
            comentarioAprendiz = null,
            isSynced = false,
            evidenciaUri = uriString,
            mimeType = mime,
            tamanoBytes = tamano,
            syncStatus = SyncStatus.PENDIENTE_CREAR.name
        )
        db.evidenciaDao().insertEvidencia(nuevaEvidencia)
        
        val actividad = db.actividadDao().getActividadById(actividadId)
        actividad?.let {
            db.actividadDao().updateActividad(it.copy(progreso = 100, syncStatus = SyncStatus.PENDIENTE_ACTUALIZAR.name))
        }
        
        nuevaEvidencia.id
    }

    open suspend fun sincronizarEvidenciaConServidor(evidenciaId: Long): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val urlRemotaGenerada = "https://xyzcompany.supabase.co/storage/v1/object/public/evidencias/dummy_$evidenciaId.jpg"
            NetworkResult.Success(urlRemotaGenerada)
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError.SinConexion, e.message)
        }
    }

    open suspend fun actualizarEstadoSincronizacion(evidenciaId: Long, estado: String, urlRemota: String = "") = withContext(Dispatchers.IO) {
        val evidencia = db.evidenciaDao().getEvidenciaById(evidenciaId)
        evidencia?.let {
            db.evidenciaDao().updateEvidencia(it.copy(syncStatus = estado, url = if(urlRemota.isNotEmpty()) urlRemota else it.url))
        }
    }

    open fun getAsistencias(userId: String): Flow<List<Asistencia>> = 
        db.asistenciaDao().getAsistenciaByUser(userId).map { list -> list.map { it.toDomain() } }
    
    open suspend fun registrarAsistencia(userId: String, estuvopresente: Boolean, observacion: String?) = withContext(Dispatchers.IO) {
        val hoy = LocalDate.now()
        val existe = db.asistenciaDao().getAsistenciaByDate(userId, hoy.toString())
        if (existe == null) {
            db.asistenciaDao().insertAsistencia(
                AsistenciaEntity(userId = userId, fecha = hoy, estuvoPresente = estuvopresente, observacion = observacion, syncStatus = SyncStatus.PENDIENTE_CREAR.name)
            )
        }
    }

    open fun getNovedades(userId: String): Flow<List<NovedadEntity>> = db.novedadDao().getNovedadesByUser(userId)
    open suspend fun saveNovedad(novedad: NovedadEntity) = withContext(Dispatchers.IO) {
        db.novedadDao().insertNovedad(novedad)
    }

    open fun getCertificados(userId: String): Flow<List<CertificadoEntity>> = db.certificadoDao().getCertificadosByUser(userId)
}
