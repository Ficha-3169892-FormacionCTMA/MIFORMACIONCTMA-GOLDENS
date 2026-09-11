package com.samuel.miformacionctma.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {
    private const val BASE_URL = "https://staging.miformacionctma.sena.edu.co/api/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Interceptor para capturar y visualizar tráfico HTTP en Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            // Adjunta el token Bearer a las peticiones protegidas
            val requestWithAuth = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer ${SessionManager.getToken()}")
                .build()
            chain.proceed(requestWithAuth)
        }
        .build()

    val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
