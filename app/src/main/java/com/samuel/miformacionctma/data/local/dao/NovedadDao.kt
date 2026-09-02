package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.NovedadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NovedadDao {
    @Query("SELECT * FROM novedades WHERE userId = :userId ORDER BY fecha DESC")
    fun getNovedadesByUser(userId: String): Flow<List<NovedadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovedad(novedad: NovedadEntity)

    @Query("SELECT * FROM novedades WHERE isSynced = 0")
    suspend fun getUnsyncedNovedades(): List<NovedadEntity>

    @Update
    suspend fun updateNovedad(novedad: NovedadEntity)
}
