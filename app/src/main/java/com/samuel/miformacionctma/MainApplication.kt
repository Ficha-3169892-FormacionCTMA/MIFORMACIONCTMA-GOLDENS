package com.samuel.miformacionctma

import android.app.Application
import com.samuel.miformacionctma.di.AppContainer
import com.samuel.miformacionctma.di.AppContainerImpl

class MainApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
