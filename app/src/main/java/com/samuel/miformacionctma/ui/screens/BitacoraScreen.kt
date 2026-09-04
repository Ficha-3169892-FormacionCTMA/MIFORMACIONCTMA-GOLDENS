package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.components.OfflineIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitacoraScreen(viewModel: AppViewModel) {
    val bitacoras by viewModel.bitacoras.collectAsState()
    
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Bitácora") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Nuevo Registro Diario", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("¿Qué hiciste hoy?") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Descripción detallada") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = horas,
                onValueChange = { horas = it },
                label = { Text("Horas dedicadas") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val h = horas.toIntOrNull() ?: 0
                    if (titulo.isNotBlank() && contenido.isNotBlank() && h > 0) {
                        viewModel.addBitacora(titulo, contenido, h)
                        titulo = ""
                        contenido = ""
                        horas = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = titulo.isNotBlank() && contenido.isNotBlank() && (horas.toIntOrNull() ?: 0) > 0
            ) {
                Text("GUARDAR REGISTRO")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Historial de Tareas", fontWeight = FontWeight.Bold)
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(bitacoras) { bitacora ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = bitacora.fecha.toString(), style = MaterialTheme.typography.labelSmall)
                                if (!bitacora.isSynced) OfflineIndicator()
                            }
                            Text(text = bitacora.titulo, fontWeight = FontWeight.Bold)
                            Text(text = bitacora.contenido, style = MaterialTheme.typography.bodySmall)
                            Text(text = "${bitacora.horas} horas dedicadas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
