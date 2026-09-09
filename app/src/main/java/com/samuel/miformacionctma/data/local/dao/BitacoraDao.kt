package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.BitacoraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {
    @Query("SELECT * FROM bitacoras WHERE userId = :userId ORDER BY fecha DESC")
    fun getBitacorasByUser(userId: String): Flow<List<BitacoraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBitacora(bitacora: BitacoraEntity)

    @Query("SELECT * FROM bitacoras WHERE isSynced = 0")
    suspend fun getUnsyncedBitacoras(): List<BitacoraEntity>

    @Update
    suspend fun updateBitacora(bitacora: BitacoraEntity)
}
