package com.samuel.miformacionctma.network

import retrofit2.Response
import retrofit2.http.*

interface ActividadApiService {
    @GET("v1/actividades")
    suspend fun getActividades(): Response<List<ActividadDto>>

    @GET("v1/actividades/{id}")
    suspend fun getActividadById(@Path("id") id: Long): Response<ActividadDto>

    @POST("v1/actividades")
    suspend fun createActividad(@Body actividad: ActividadDto): Response<ActividadDto>

    @PUT("v1/actividades/{id}")
    suspend fun updateActividad(@Path("id") id: Long, @Body actividad: ActividadDto): Response<ActividadDto>
}
