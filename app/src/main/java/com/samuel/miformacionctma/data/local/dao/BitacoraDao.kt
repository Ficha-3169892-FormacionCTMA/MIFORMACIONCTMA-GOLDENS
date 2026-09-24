package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.BitacoraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {
    @Query("SELECT * FROM bitacoras WHERE autorId = :autorId ORDER BY fecha DESC")
    fun getBitacorasByAutor(autorId: String): Flow<List<BitacoraEntity>>

    @Query("SELECT * FROM bitacoras WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getBitacoraByRemoteId(remoteId: Long): BitacoraEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBitacora(bitacora: BitacoraEntity)

    @Query("SELECT * FROM bitacoras WHERE isSynced = 0")
    suspend fun getUnsyncedBitacoras(): List<BitacoraEntity>

    @Update
    suspend fun updateBitacora(bitacora: BitacoraEntity)

    @Query("DELETE FROM bitacoras WHERE id = :id")
    suspend fun deleteBitacoraById(id: Long)
}
