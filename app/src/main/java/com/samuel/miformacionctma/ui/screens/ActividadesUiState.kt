package com.samuel.miformacionctma.ui.screens

import com.samuel.miformacionctma.model.ActividadFormativa

/**
 * Representa los estados posibles de la lista de actividades.
 */
sealed interface ListadoUiState {
    object Cargando : ListadoUiState
    
    data class Contenido(
        val actividades: List<ActividadFormativa>
    ) : ListadoUiState
    
    object Vacio : ListadoUiState
    
    data class Error(
        val mensaje: String,
        val accionReintento: () -> Unit
    ) : ListadoUiState
}

/**
 * Representa los estados de una operación asíncrona (Guardar/Eliminar).
 */
sealed interface OperacionUiState {
    object Inactiva : OperacionUiState
    object EnCurso : OperacionUiState
    object Exitosa : OperacionUiState
    data class Fallida(val mensaje: String) : OperacionUiState
}
