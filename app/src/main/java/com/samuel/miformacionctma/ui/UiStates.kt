package com.samuel.miformacionctma.ui

import com.samuel.miformacionctma.model.ActividadFormativa

sealed interface ListadoUiState {
    object Cargando : ListadoUiState
    data class Contenido(val data: List<ActividadFormativa>) : ListadoUiState
    object Vacio : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

sealed interface OperacionUiState {
    object Inactiva : OperacionUiState
    object EnCurso : OperacionUiState
    object Exitosa : OperacionUiState
    data class Fallida(val mensaje: String) : OperacionUiState
}
