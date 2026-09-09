package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY fechaFin ASC")
    fun getAllActividades(): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE titulo LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%' ORDER BY fechaFin ASC")
    fun searchActividades(query: String): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun getActividadById(id: Long): ActividadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividades(actividades: List<ActividadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(actividad: ActividadEntity)

    @Update
    suspend fun updateActividad(actividad: ActividadEntity)

    @Delete
    suspend fun deleteActividad(actividad: ActividadEntity)
}
