package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.components.OfflineIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenciaScreen(viewModel: AppViewModel, navController: NavController) {
    val asistencias by viewModel.asistencias.collectAsState()
    var showMockScanner by remember { mutableStateOf(false) }
    var mockQrCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Asistencia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Registrar Asistencia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Escanea el código QR proyectado por el instructor", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showMockScanner = true }) {
                        Text("ABRIR ESCÁNER")
                    }
                }
            }

            if (showMockScanner) {
                AlertDialog(
                    onDismissRequest = { showMockScanner = false },
                    title = { Text("Simulador de Escáner QR") },
                    text = {
                        Column {
                            Text("Ingresa el código del QR (Simulación)")
                            OutlinedTextField(
                                value = mockQrCode,
                                onValueChange = { mockQrCode = it },
                                placeholder = { Text("SENA_SESSION_123") }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.scanQRAsistencia(mockQrCode)
                            showMockScanner = false
                            mockQrCode = ""
                        }) { Text("ESCANEAR") }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Mi Historial de Asistencia", fontWeight = FontWeight.Bold)
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(asistencias) { registro ->
                    ListItem(
                        headlineContent = { Text("Sesión: ${registro.fecha}") },
                        supportingContent = { Text(registro.observacion ?: "Sin observaciones") },
                        trailingContent = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!registro.isSynced) OfflineIndicator()
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (registro.estuvoPresente) "PRESENTE" else "INASISTENCIA",
                                    color = if (registro.estuvoPresente) Color(0xFF39A900) else Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
