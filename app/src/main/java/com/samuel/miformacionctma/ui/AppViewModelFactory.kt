package com.samuel.miformacionctma.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samuel.miformacionctma.MainApplication

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            val container = (application as MainApplication).container
            return AppViewModel(application, container) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
