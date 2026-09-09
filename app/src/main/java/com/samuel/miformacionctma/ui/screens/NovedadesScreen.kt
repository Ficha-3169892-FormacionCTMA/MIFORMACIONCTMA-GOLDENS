package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.components.OfflineIndicator
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovedadesScreen(viewModel: AppViewModel, navController: NavController) {
    val novedades by viewModel.novedades.collectAsState()
    
    var tipo by remember { mutableStateOf("MEDICA") }
    var motivo by remember { mutableStateOf("") }
    val tipos = listOf("MEDICA", "PERMISO", "OTRA")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Novedad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Nueva Solicitud", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Tipo de Novedad:")
            Row {
                tipos.forEach { t ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = tipo == t, onClick = { tipo = t })
                        Text(t)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo / Explicación") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (motivo.isNotBlank()) {
                        viewModel.addNovedad(tipo, motivo, LocalDate.now(), null)
                        motivo = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = motivo.isNotBlank()
            ) {
                Text("ENVIAR REPORTE")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Mis Novedades Reportadas", fontWeight = FontWeight.Bold)
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(novedades) { novedad ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = novedad.fecha.toString(), style = MaterialTheme.typography.labelSmall)
                                Badge(
                                    containerColor = when(novedad.estado) {
                                        "APROBADA" -> Color(0xFF39A900)
                                        "RECHAZADA" -> Color.Red
                                        "REVISION" -> Color(0xFFFFA500) // Reemplazado Orange (unresolved) por su valor Hex
                                        else -> Color.Gray
                                    }
                                ) {
                                    Text(novedad.estado, color = Color.White)
                                }
                            }
                            Text(text = "Tipo: ${novedad.tipo}", fontWeight = FontWeight.Bold)
                            Text(text = novedad.motivo, style = MaterialTheme.typography.bodySmall)
                            if (!novedad.isSynced) {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    OfflineIndicator()
                                    Text(" Pendiente de envío", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
