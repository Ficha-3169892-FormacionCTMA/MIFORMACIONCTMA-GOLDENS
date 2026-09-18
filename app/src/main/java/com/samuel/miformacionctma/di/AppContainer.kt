package com.samuel.miformacionctma.di

import android.content.Context
import com.samuel.miformacionctma.BuildConfig
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.network.ActividadApiService
import com.samuel.miformacionctma.network.ApiClient
import com.samuel.miformacionctma.network.RemoteActividadDataSource
import com.samuel.miformacionctma.network.TokenProvider

/**
 * Contenedor de dependencias (Service Locator) para el suministro limpio de instancias.
 */
interface AppContainer {
    val database: AppDatabase
    val remoteDataSource: RemoteActividadDataSource
    val appRepository: AppRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val tokenProvider: TokenProvider by lazy {
        object : TokenProvider {
            override fun getToken(): String? = "MOCK_TOKEN"
        }
    }

    private val apiClient: ApiClient by lazy {
        ApiClient(tokenProvider)
    }

    private val apiService: ActividadApiService by lazy {
        apiClient.createService<ActividadApiService>()
    }

    override val remoteDataSource: RemoteActividadDataSource by lazy {
        RemoteActividadDataSource(apiService)
    }

    override val appRepository: AppRepository by lazy {
        AppRepository(database, remoteDataSource)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}
