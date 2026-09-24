package com.samuel.miformacionctma

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            val factory = AppViewModelFactory(context.applicationContext as Application)
            
            val appViewModel: AppViewModel = viewModel(factory = factory)
            val authViewModel: AuthViewModel = viewModel(factory = factory)

            val userId by appViewModel.userId.collectAsState()
            val themeMode by appViewModel.themeMode.collectAsState()
            val fontSizeScale by appViewModel.fontSizeScale.collectAsState()

            val isDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

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
                        val authNavController = rememberNavController()
                        NavHost(navController = authNavController, startDestination = "login") {
                            composable("login") {
                                LoginScreen(
                                    authViewModel = authViewModel,
                                    onNavigateToRegister = { authNavController.navigate("register") }
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    authViewModel = authViewModel,
                                    onNavigateBackToLogin = { authNavController.popBackStack() },
                                    onRegisterSuccess = { authNavController.popBackStack() }
                                )
                            }
                        }
                    } else {
                        MainScreen(appViewModel)
                    }
                }
            }
        }
    }
}
