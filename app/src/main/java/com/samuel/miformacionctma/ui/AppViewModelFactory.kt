package com.samuel.miformacionctma.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samuel.miformacionctma.MainApplication

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = (application as MainApplication).container
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(application, container) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(container.authRepository) as T
        }
        if (modelClass.isAssignableFrom(RevisionViewModel::class.java)) {
            return RevisionViewModel(container.revisionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
