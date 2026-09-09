package com.samuel.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.MainScreen
import com.samuel.miformacionctma.ui.screens.LoginScreen
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppViewModel = viewModel()
            val userId by viewModel.userId.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()
            val fontSizeScale by viewModel.fontSizeScale.collectAsState()

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
                // Aplicar tipografía personalizada envolviendo el contenido
                MaterialTheme(typography = customTypography) {
                    if (userId == null) {
                        LoginScreen(viewModel)
                    } else {
                        MainScreen(viewModel)
                    }
                }
            }
        }
    }
}
