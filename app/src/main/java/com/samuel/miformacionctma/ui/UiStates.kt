package com.samuel.miformacionctma.ui

import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.network.NetworkError

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
    data class Fallida(val error: NetworkError, val mensaje: String? = null) : OperacionUiState
}
