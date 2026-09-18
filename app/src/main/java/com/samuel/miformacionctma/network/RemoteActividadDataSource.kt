package com.samuel.miformacionctma.network

import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class RemoteActividadDataSource(private val apiService: ActividadApiService) {

    suspend fun getActividades(): NetworkResult<List<ActividadDto>> {
        return handleApiCall { apiService.getActividades() }
    }

    suspend fun getActividadById(id: Long): NetworkResult<ActividadDto> {
        return handleApiCall { apiService.getActividadById(id) }
    }

    private suspend fun <T> handleApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(NetworkError.Desconocido, "Cuerpo de respuesta vacío")
                }
            } else {
                val errorType = when (response.code()) {
                    401 -> NetworkError.NoAutorizado
                    404 -> NetworkError.NoEncontrado
                    in 500..599 -> NetworkError.ErrorServidor
                    else -> NetworkError.Desconocido
                }
                NetworkResult.Error(errorType, response.message())
            }
        } catch (e: SocketTimeoutException) {
            NetworkResult.Error(NetworkError.Timeout, e.message)
        } catch (e: IOException) {
            NetworkResult.Error(NetworkError.SinConexion, e.message)
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError.Desconocido, e.message)
        }
    }
}
