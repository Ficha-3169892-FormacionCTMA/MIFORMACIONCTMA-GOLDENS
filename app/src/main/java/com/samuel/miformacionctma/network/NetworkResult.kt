package com.samuel.miformacionctma.network

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val errorType: NetworkError, val message: String? = null) : NetworkResult<Nothing>()
}

sealed class NetworkError {
    object SinConexion : NetworkError()
    object Timeout : NetworkError()
    object NoAutorizado : NetworkError()
    object NoEncontrado : NetworkError()
    object ErrorServidor : NetworkError()
    object ErrorSerializacion : NetworkError()
    object Desconocido : NetworkError()
}
