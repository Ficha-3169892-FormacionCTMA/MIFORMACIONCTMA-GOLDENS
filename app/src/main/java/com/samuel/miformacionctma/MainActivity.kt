package com.samuel.miformacionctma

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.AppViewModelFactory
import com.samuel.miformacionctma.ui.AuthViewModel
import com.samuel.miformacionctma.ui.MainScreen
import com.samuel.miformacionctma.ui.screens.LoginScreen
import com.samuel.miformacionctma.ui.screens.RegisterScreen
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val application = context.applicationContext as Application
            
            // ViewModel para la lógica general y estado de sesión
            val appViewModel: AppViewModel = viewModel(
                factory = AppViewModelFactory(application)
            )
            
            // ViewModel para el flujo de autenticación (Login/Registro)
            val authViewModel: AuthViewModel = viewModel(
                factory = AppViewModelFactory(application)
            )

            val userId by appViewModel.userId.collectAsState()
            val themeMode by appViewModel.themeMode.collectAsState()
            val fontSizeScale by appViewModel.fontSizeScale.collectAsState()

            // Estado de navegación local para alternar entre Login y Registro
            var showRegister by remember { mutableStateOf(false) }

            val isDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            // Adaptar tipografía según preferencia de accesibilidad (HU-15)
            val baseTypography = MaterialTheme.typography
            val scaleFactor = when(fontSizeScale) {
                "SMALL" -> 0.8f
                "LARGE" -> 1.2f
                else -> 1.0f
            }
            
            val customTypography = Typography(
                headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * scaleFactor),
                headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * scaleFactor),
                titleLarge = baseTypography.titleLarge.copy(fontSize = baseTypography.titleLarge.fontSize * scaleFactor),
                bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * scaleFactor),
                bodyMedium = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * scaleFactor)
            )

            MiFormacionCTMATheme(darkTheme = isDarkTheme) {
                MaterialTheme(typography = customTypography) {
                    if (userId == null) {
                        // Flujo de Autenticación
                        if (showRegister) {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = { 
                                    authViewModel.resetState()
                                    showRegister = false 
                                }
                            )
                        } else {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = { 
                                    authViewModel.resetState()
                                    showRegister = true 
                                }
                            )
                        }
                    } else {
                        // App principal (ya autenticado)
                        MainScreen(appViewModel)
                    }
                }
            }
        }
    }
}
