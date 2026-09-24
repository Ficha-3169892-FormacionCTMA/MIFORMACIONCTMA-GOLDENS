package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.AsistenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDao {
    @Query("SELECT * FROM asistencia WHERE aprendizId = :aprendizId ORDER BY fecha DESC")
    fun getAsistenciaByAprendiz(aprendizId: String): Flow<List<AsistenciaEntity>>

    @Query("SELECT * FROM asistencia WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getAsistenciaByRemoteId(remoteId: Long): AsistenciaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistencia(asistencia: AsistenciaEntity)

    @Query("SELECT * FROM asistencia WHERE aprendizId = :aprendizId AND fecha = :fecha LIMIT 1")
    suspend fun getAsistenciaByDate(aprendizId: String, fecha: String): AsistenciaEntity?

    @Query("SELECT * FROM asistencia WHERE isSynced = 0")
    suspend fun getUnsyncedAsistencia(): List<AsistenciaEntity>

    @Update
    suspend fun updateAsistencia(asistencia: AsistenciaEntity)
}
