package com.samuel.miformacionctma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val rol: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signIn(email, password)
                .onSuccess { rol ->
                    _uiState.value = AuthUiState.Success(rol)
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error("Correo o contraseña incorrectos")
                }
        }
    }

    fun register(email: String, password: String, confirmPass: String, rol: String) {
        if (email.isBlank() || password.isBlank() || confirmPass.isBlank()) {
            _uiState.value = AuthUiState.Error("Por favor completa todos los campos")
            return
        }

        if (!email.lowercase().endsWith("@sena.edu.co")) {
            _uiState.value = AuthUiState.Error("El correo debe ser @sena.edu.co")
            return
        }

        if (password != confirmPass) {
            _uiState.value = AuthUiState.Error("Las contraseñas no coinciden")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signUp(email, password, rol.lowercase())
                .onSuccess {
                    _uiState.value = AuthUiState.Success(rol.lowercase())
                }
                .onFailure { e ->
                    val errorMsg = when {
                        e.message?.contains("already registered", true) == true -> "El correo ya está registrado"
                        else -> "Error en el registro. Intenta de nuevo."
                    }
                    _uiState.value = AuthUiState.Error(errorMsg)
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
