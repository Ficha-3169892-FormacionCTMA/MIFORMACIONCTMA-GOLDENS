package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.components.OfflineIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividadScreen(id: Long, viewModel: AppViewModel, navController: NavController) {
    val actividades by viewModel.actividades.collectAsState()
    val actividad = actividades.find { it.id == id }
    val evidencias by viewModel.getEvidencias(id).collectAsState(initial = emptyList())
    val userRole by viewModel.userRole.collectAsState()

    var urlEvidencia by remember { mutableStateOf("") }

    if (actividad == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Actividad no encontrada")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Text(text = actividad.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(onClick = {}, label = { Text(actividad.prioridad.name) })
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(if (actividad.progreso == 100) "COMPLETADA" else "EN PROCESO") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = if (actividad.progreso == 100) Color(0xFF39A900) else Color.Unspecified
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Descripción", fontWeight = FontWeight.Bold)
                Text(text = actividad.descripcion ?: "Sin descripción adicional.")
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (userRole == "LEARNER") {
                item {
                    Text(text = "Entregar Evidencia", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = urlEvidencia,
                        onValueChange = { urlEvidencia = it },
                        label = { Text("Enlace de la evidencia (URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://...") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (urlEvidencia.startsWith("http")) {
                                viewModel.submitEvidencia(actividad.id, urlEvidencia)
                                urlEvidencia = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = urlEvidencia.startsWith("http"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A900))
                    ) {
                        Text("SUBIR EVIDENCIA")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                Text(text = "Evidencias Enviadas", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (evidencias.isEmpty()) {
                item {
                    Text("No has enviado evidencias aún.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            } else {
                items(evidencias) { evidencia ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = evidencia.fechaEntrega.toString().replace("T", " "), style = MaterialTheme.typography.labelSmall)
                                if (!evidencia.isSynced) OfflineIndicator()
                            }
                            Text(text = evidencia.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                            
                            // HU-11: Retroalimentación
                            if (evidencia.comentarioAprendiz != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Comentario del Instructor:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        Text(evidencia.comentarioAprendiz, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
