package com.samuel.miformacionctma.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val rol: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isSessionActive: Boolean get() = authRepository.isSessionActive

    fun registrarUsuario(email: String, password: String, confirmPassword: String, rol: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Todos los campos son obligatorios")
            return
        }

        if (!email.trim().endsWith("@sena.edu.co", ignoreCase = true)) {
            _uiState.value = AuthUiState.Error("El correo debe terminar exactamente en @sena.edu.co")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Las contraseñas no coinciden")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signUp(email.trim(), password, rol)
                .onSuccess {
                    _uiState.value = AuthUiState.Success(rol)
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error("Error al registrarse. Verifique si el correo ya existe.")
                }
        }
    }

    fun iniciarSesion(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Todos los campos son obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signIn(email.trim(), password)
                .onSuccess { rol ->
                    _uiState.value = AuthUiState.Success(rol)
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error("Correo o contraseña incorrectos")
                }
        }
    }

    fun restablecerEstado() {
        _uiState.value = AuthUiState.Idle
    }
}
