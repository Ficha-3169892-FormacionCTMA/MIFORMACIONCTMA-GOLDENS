package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel, navController: NavController) {
    val themeMode by viewModel.themeMode.collectAsState()
    val fontSizeScale by viewModel.fontSizeScale.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(text = "Apariencia", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Tema de la aplicación")
            Row {
                ThemeOption("Claro", themeMode == "LIGHT") { viewModel.updateTheme("LIGHT") }
                ThemeOption("Oscuro", themeMode == "DARK") { viewModel.updateTheme("DARK") }
                ThemeOption("Sistema", themeMode == "SYSTEM") { viewModel.updateTheme("SYSTEM") }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Accesibilidad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Tamaño de fuente")
            Row {
                FontSizeOption("Pequeña", fontSizeScale == "SMALL") { viewModel.updateFontSize("SMALL") }
                FontSizeOption("Mediana", fontSizeScale == "MEDIUM") { viewModel.updateFontSize("MEDIUM") }
                FontSizeOption("Grande", fontSizeScale == "LARGE") { viewModel.updateFontSize("LARGE") }
            }
        }
    }
}

@Composable
fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun FontSizeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
        Spacer(modifier = Modifier.width(8.dp))
    }
}
