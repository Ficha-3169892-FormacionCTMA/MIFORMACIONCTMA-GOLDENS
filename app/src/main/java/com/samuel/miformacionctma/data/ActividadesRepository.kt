package com.samuel.miformacionctma.data

import com.samuel.miformacionctma.model.ActividadFormativa
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio para abstraer el origen de datos (Room, API, etc.)
 */
interface ActividadesRepository {
    /**
     * Retorna un flujo de actividades filtradas por título.
     */
    fun getActividadesStream(query: String): Flow<List<ActividadFormativa>>

    /**
     * Guarda una actividad en la persistencia.
     */
    suspend fun guardarActividad(actividad: ActividadFormativa)

    /**
     * Elimina una actividad de la persistencia.
     */
    suspend fun eliminarActividad(actividad: ActividadFormativa)
}
