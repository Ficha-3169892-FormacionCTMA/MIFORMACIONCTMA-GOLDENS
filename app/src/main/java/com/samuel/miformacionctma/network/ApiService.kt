package com.samuel.miformacionctma.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("notas/{aprendizId}")
    suspend fun obtenerNotasAprendiz(@Path("aprendizId") aprendizId: String): Response<Unit>

    @POST("bitacora/subir")
    suspend fun subirBitacora(@Body fileData: String): Response<Unit>
}