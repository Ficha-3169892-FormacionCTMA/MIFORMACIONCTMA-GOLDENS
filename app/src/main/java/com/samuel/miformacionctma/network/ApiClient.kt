package com.samuel.miformacionctma.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://staging.miformacionctma.sena.edu.co/api/"

    // Interceptor para capturar y visualizar tráfico HTTP en Logcat (DevTools Network)
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
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}