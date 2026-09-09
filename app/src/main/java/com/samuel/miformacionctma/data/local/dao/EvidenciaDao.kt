package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.EvidenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {
    @Query("SELECT * FROM evidencias WHERE actividadId = :actividadId")
    fun getEvidenciasByActividad(actividadId: Long): Flow<List<EvidenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidencia(evidencia: EvidenciaEntity)

    @Query("SELECT * FROM evidencias WHERE isSynced = 0")
    suspend fun getUnsyncedEvidencias(): List<EvidenciaEntity>

    @Update
    suspend fun updateEvidencia(evidencia: EvidenciaEntity)
}
