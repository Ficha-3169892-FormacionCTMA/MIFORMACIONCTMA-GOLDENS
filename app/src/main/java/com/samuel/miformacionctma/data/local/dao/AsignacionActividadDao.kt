package com.samuel.miformacionctma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samuel.miformacionctma.data.local.entities.AsignacionActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignacionActividadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignaciones(lista: List<AsignacionActividadEntity>)

    @Query("SELECT id FROM asignacion_actividad WHERE actividadId = :actividadId AND aprendizId = :aprendizId LIMIT 1")
    suspend fun getAsignacionId(actividadId: Long, aprendizId: String): Long?

    @Query("SELECT * FROM asignacion_actividad WHERE aprendizId = :aprendizId")
    fun getAsignacionesByAprendiz(aprendizId: String): Flow<List<AsignacionActividadEntity>>
}
