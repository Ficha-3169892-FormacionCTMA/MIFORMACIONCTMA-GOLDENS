package com.samuel.miformacionctma.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel, navController: NavController) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val fontSizeScale by viewModel.fontSizeScale.collectAsState()
    val notificacionesEnabled by viewModel.notificacionesEnabled.collectAsState()

    // Lanzador para solicitar el permiso POST_NOTIFICATIONS en Android 13+ (Paso 7)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.setNotificacionesEnabled(concedido)
        if (concedido) {
            Toast.makeText(context, "¡Recordatorios activados correctamente!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Los recordatorios visuales están desactivados.", Toast.LENGTH_SHORT).show()
        }
    }

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

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Alertas y Recordatorios", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Activa los avisos locales para recibir notificaciones sobre tus próximas entregas y estados de sincronización de evidencias.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Switch(
                    checked = notificacionesEnabled,
                    onCheckedChange = { activar ->
                        if (activar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // Se solicita POST_NOTIFICATIONS solo en Android 13+ y solo después de que la persona decida activarlo
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setNotificacionesEnabled(activar)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = if (notificacionesEnabled) "Recordatorios habilitados" else "Recordatorios deshabilitados")
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
