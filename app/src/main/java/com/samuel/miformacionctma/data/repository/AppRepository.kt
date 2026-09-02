package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.entities.*
import com.samuel.miformacionctma.model.Prioridad
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class AppRepository(private val db: AppDatabase) {

    // --- Actividades (HU-01, HU-02, HU-09) ---
    fun getActividades(): Flow<List<ActividadEntity>> = db.actividadDao().getAllActividades()
    
    suspend fun getActividadById(id: Long) = db.actividadDao().getActividadById(id)
    
    suspend fun saveActividad(actividad: ActividadEntity) = db.actividadDao().insertActividad(actividad)

    // --- Bitacoras (HU-04) ---
    fun getBitacoras(userId: String): Flow<List<BitacoraEntity>> = db.bitacoraDao().getBitacorasByUser(userId)
    
    suspend fun saveBitacora(bitacora: BitacoraEntity) = db.bitacoraDao().insertBitacora(bitacora)

    // --- Evidencias (HU-03, HU-11) ---
    fun getEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> = db.evidenciaDao().getEvidenciasByActividad(actividadId)
    
    suspend fun saveEvidencia(evidencia: EvidenciaEntity) {
        db.evidenciaDao().insertEvidencia(evidencia)
        // Auto-complete activity if evidence is submitted (HU-03)
        val actividad = db.actividadDao().getActividadById(evidencia.actividadId)
        actividad?.let {
            db.actividadDao().updateActividad(it.copy(progreso = 100))
        }
    }

    // --- Asistencia (HU-05) ---
    fun getAsistencias(userId: String): Flow<List<AsistenciaEntity>> = db.asistenciaDao().getAsistenciaByUser(userId)
    
    suspend fun registrarAsistencia(userId: String, estuvopresente: Boolean, observacion: String?) {
        val hoy = LocalDate.now()
        val existe = db.asistenciaDao().getAsistenciaByDate(userId, hoy.toString())
        if (existe == null) {
            db.asistenciaDao().insertAsistencia(
                AsistenciaEntity(userId = userId, fecha = hoy, estuvoPresente = estuvopresente, observacion = observacion)
            )
        }
    }

    // --- Novedades (HU-14) ---
    fun getNovedades(userId: String): Flow<List<NovedadEntity>> = db.novedadDao().getNovedadesByUser(userId)
    
    suspend fun saveNovedad(novedad: NovedadEntity) = db.novedadDao().insertNovedad(novedad)

    // --- Certificados (HU-13) ---
    fun getCertificados(userId: String): Flow<List<CertificadoEntity>> = db.certificadoDao().getCertificadosByUser(userId)
    
    suspend fun saveCertificado(certificado: CertificadoEntity) = db.certificadoDao().insertCertificado(certificado)

    // --- Auth & Users (HU-06) ---
    fun getUser(userId: String): Flow<UserEntity?> = db.userDao().getUserById(userId)
    
    suspend fun saveUser(user: UserEntity) = db.userDao().insertUser(user)
    
    suspend fun clearSession() = db.userDao().clearAll()
    
    // --- Offline Sync (HU-10) ---
    suspend fun getUnsyncedData(): UnsyncedData {
        return UnsyncedData(
            bitacoras = db.bitacoraDao().getUnsyncedBitacoras(),
            evidencias = db.evidenciaDao().getUnsyncedEvidencias(),
            asistencia = db.asistenciaDao().getUnsyncedAsistencia(),
            novedades = db.novedadDao().getUnsyncedNovedades()
        )
    }
}

data class UnsyncedData(
    val bitacoras: List<BitacoraEntity>,
    val evidencias: List<EvidenciaEntity>,
    val asistencia: List<AsistenciaEntity>,
    val novedades: List<NovedadEntity>
)
