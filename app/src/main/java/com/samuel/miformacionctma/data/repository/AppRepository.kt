package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.data.remote.RemoteActividadDataSource
import com.samuel.miformacionctma.data.remote.dto.toEntity
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AppRepository(
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

    fun getActividadesStream(query: String = ""): Flow<List<ActividadFormativa>> {
        val flow = if (query.isEmpty()) {
            db.actividadDao().getAllActividades()
        } else {
            db.actividadDao().searchActividades(query)
        }
        return flow.map { list -> list.map { it.toDomain() } }
    }

    /**
     * Sincroniza las actividades desde el servicio remoto a la base de datos local.
     * Implementa resiliencia: si falla la red, no borra el caché.
     */
    suspend fun refreshActividades() = withContext(Dispatchers.IO) {
        try {
            val response = remoteDataSource.getActividades()
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val entities = dtos.map { it.toEntity() }
                
                // Operación atómica en Room
                db.actividadDao().insertActividades(entities)
            } else {
                val errorMsg = when(response.code()) {
                    401 -> "Sesión expirada"
                    in 500..599 -> "Error en el servidor"
                    else -> "Error desconocido: ${response.code()}"
                }
                throw Exception(errorMsg)
            }
        } catch (ce: CancellationException) {
            throw ce // Regla: relanzar para no romper cancelación
        } catch (e: IOException) {
            throw Exception("Sin conexión a internet o tiempo de espera agotado")
        } catch (e: Exception) {
            throw Exception("Fallo en la actualización: ${e.message}")
        }
    }
    
    suspend fun getActividadById(id: Long): ActividadFormativa? = withContext(Dispatchers.IO) {
        db.actividadDao().getActividadById(id)?.toDomain()
    }
    
    suspend fun saveActividad(actividad: ActividadFormativa) = withContext(Dispatchers.IO) {
        db.actividadDao().insertActividad(actividad.toEntity())
    }

    suspend fun deleteActividad(actividad: ActividadFormativa) = withContext(Dispatchers.IO) {
        db.actividadDao().deleteActividad(actividad.toEntity())
    }

    // --- Otros métodos ---
    fun getBitacoras(userId: String): Flow<List<BitacoraEntity>> = db.bitacoraDao().getBitacorasByUser(userId)
    
    suspend fun saveBitacora(bitacora: BitacoraEntity) = withContext(Dispatchers.IO) {
        db.bitacoraDao().insertBitacora(bitacora)
    }

    fun getEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> = db.evidenciaDao().getEvidenciasByActividad(actividadId)
    
    suspend fun saveEvidencia(evidencia: EvidenciaEntity) = withContext(Dispatchers.IO) {
        db.evidenciaDao().insertEvidencia(evidencia)
        val actividad = db.actividadDao().getActividadById(evidencia.actividadId)
        actividad?.let {
            db.actividadDao().updateActividad(it.copy(progreso = 100))
        }
    }

    fun getAsistencias(userId: String): Flow<List<AsistenciaEntity>> = db.asistenciaDao().getAsistenciaByUser(userId)

    suspend fun registrarAsistencia(userId: String, estuvopresente: Boolean, observacion: String?) = withContext(Dispatchers.IO) {
        val hoy = LocalDate.now()
        val existe = db.asistenciaDao().getAsistenciaByDate(userId, hoy.toString())
        if (existe == null) {
            db.asistenciaDao().insertAsistencia(
                AsistenciaEntity(userId = userId, fecha = hoy, estuvoPresente = estuvopresente, observacion = observacion)
            )
        }
    }

    fun getNovedades(userId: String): Flow<List<NovedadEntity>> = db.novedadDao().getNovedadesByUser(userId)

    fun getCertificados(userId: String): Flow<List<CertificadoEntity>> = db.certificadoDao().getCertificadosByUser(userId)
}
