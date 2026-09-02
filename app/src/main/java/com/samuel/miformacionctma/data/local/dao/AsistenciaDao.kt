package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.AsistenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDao {
    @Query("SELECT * FROM asistencia WHERE userId = :userId ORDER BY fecha DESC")
    fun getAsistenciaByUser(userId: String): Flow<List<AsistenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistencia(asistencia: AsistenciaEntity)

    @Query("SELECT * FROM asistencia WHERE userId = :userId AND fecha = :fecha LIMIT 1")
    suspend fun getAsistenciaByDate(userId: String, fecha: String): AsistenciaEntity?

    @Query("SELECT * FROM asistencia WHERE isSynced = 0")
    suspend fun getUnsyncedAsistencia(): List<AsistenciaEntity>

    @Update
    suspend fun updateAsistencia(asistencia: AsistenciaEntity)
}
