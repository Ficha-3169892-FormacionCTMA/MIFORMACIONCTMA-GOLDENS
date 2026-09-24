package com.samuel.miformacionctma.di

import android.content.Context
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.data.repository.AuthRepository
import com.samuel.miformacionctma.data.repository.RevisionRepository
import com.samuel.miformacionctma.network.RemoteActividadDataSource

/**
 * Contenedor de dependencias (Service Locator) para el suministro limpio de instancias.
 */
interface AppContainer {
    val database: AppDatabase
    val remoteDataSource: RemoteActividadDataSource
    val appRepository: AppRepository
    val userPreferencesRepository: UserPreferencesRepository
    val authRepository: AuthRepository
    val revisionRepository: RevisionRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val remoteDataSource: RemoteActividadDataSource by lazy {
        RemoteActividadDataSource()
    }

    override val appRepository: AppRepository by lazy {
        AppRepository(database, remoteDataSource)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(userPreferencesRepository)
    }

    override val revisionRepository: RevisionRepository by lazy {
        RevisionRepository()
    }
}
