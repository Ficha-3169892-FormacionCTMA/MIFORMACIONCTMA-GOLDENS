package com.samuel.miformacionctma.data.remote

import com.samuel.miformacionctma.data.remote.dto.ActividadDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class RemoteActividadDataSource(
    private val tokenProvider: TokenProvider
) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
            tokenProvider.getToken()?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        }
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.miformacionctma.com/") // Base URL ficticia
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val apiService = retrofit.create(ActividadApiService::class.java)

    suspend fun getActividades(): Response<List<ActividadDto>> = apiService.getActividades()
    
    suspend fun getActividadById(id: Long): Response<ActividadDto> = apiService.getActividadById(id)

    suspend fun createActividad(actividad: ActividadDto): Response<ActividadDto> = apiService.createActividad(actividad)

    suspend fun updateActividad(id: Long, actividad: ActividadDto): Response<ActividadDto> = apiService.updateActividad(id, actividad)
}
