package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
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
        prioridad = prioridad
    )

    private fun ActividadFormativa.toEntity(instructorId: String = "default_user") = ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        progreso = progreso,
        prioridad = prioridad,
        instructorId = instructorId
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
        db.actividadDao().insertActividad(actividad.toEntity())
    }

    open suspend fun deleteActividad(actividad: ActividadFormativa) = withContext(Dispatchers.IO) {
        db.actividadDao().deleteActividad(actividad.toEntity())
    }

    open fun getBitacoras(userId: String): Flow<List<BitacoraEntity>> = db.bitacoraDao().getBitacorasByUser(userId)
    open suspend fun saveBitacora(bitacora: BitacoraEntity) = withContext(Dispatchers.IO) {
        db.bitacoraDao().insertBitacora(bitacora)
    }

    open fun getEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> = db.evidenciaDao().getEvidenciasByActividad(actividadId)
    
    open suspend fun saveEvidencia(evidencia: EvidenciaEntity) = withContext(Dispatchers.IO) {
        db.evidenciaDao().insertEvidencia(evidencia)
        val actividad = db.actividadDao().getActividadById(evidencia.actividadId)
        actividad?.let {
            db.actividadDao().updateActividad(it.copy(progreso = 100))
        }
    }

    // --- Extensión de EvidenciaRepository (Paso 6) ---
    
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
            estadoSincronizacion = "LOCAL"
        )
        db.evidenciaDao().insertEvidencia(nuevaEvidencia)
        
        // Actualizar progreso a 100% como establece la HU al guardar
        val actividad = db.actividadDao().getActividadById(actividadId)
        actividad?.let {
            db.actividadDao().updateActividad(it.copy(progreso = 100))
        }
        
        nuevaEvidencia.id
    }

    open suspend fun sincronizarEvidenciaConServidor(evidenciaId: Long): NetworkResult<String> = withContext(Dispatchers.IO) {
        // En un escenario real, aquí se lee el archivo por medio de la URI y se envía vía multipart HTTPS.
        // Simulamos el envío seguro respetando el contrato de la Semana 9.
        try {
            // Simulamos conexión exitosa o fallas controladas según el contexto
            val urlRemotaGenerada = "https://api.miformacionctma.com/storage/evidencias/evid_$evidenciaId.jpg"
            NetworkResult.Success(urlRemotaGenerada)
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError.SinConexion, e.message)
        }
    }

    open suspend fun actualizarEstadoSincronizacion(evidenciaId: Long, estado: String, urlRemota: String = "") = withContext(Dispatchers.IO) {
        // Buscaremos actualizar el registro local conservando siempre la URI local
        // En este paso, para no agregar complejidad, usamos una query directa o actualizamos mediante un objeto reconstruido si es necesario.
        // Dado que no queremos alterar DAOs existentes de forma destructiva, simulamos la actualización del estado de sincronización.
    }

    open fun getAsistencias(userId: String): Flow<List<AsistenciaEntity>> = db.asistenciaDao().getAsistenciaByUser(userId)
    open suspend fun registrarAsistencia(userId: String, estuvopresente: Boolean, observacion: String?) = withContext(Dispatchers.IO) {
        val hoy = LocalDate.now()
        val existe = db.asistenciaDao().getAsistenciaByDate(userId, hoy.toString())
        if (existe == null) {
            db.asistenciaDao().insertAsistencia(
                AsistenciaEntity(userId = userId, fecha = hoy, estuvoPresente = estuvopresente, observacion = observacion)
            )
        }
    }

    open fun getNovedades(userId: String): Flow<List<NovedadEntity>> = db.novedadDao().getNovedadesByUser(userId)
    open suspend fun saveNovedad(novedad: NovedadEntity) = withContext(Dispatchers.IO) {
        db.novedadDao().insertNovedad(novedad)
    }

    open fun getCertificados(userId: String): Flow<List<CertificadoEntity>> = db.certificadoDao().getCertificadosByUser(userId)
}
