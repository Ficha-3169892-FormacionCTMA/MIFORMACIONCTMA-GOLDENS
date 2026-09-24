package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.data.local.entities.NovedadEntity
import com.samuel.miformacionctma.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovedadesRecibidasScreen(viewModel: AppViewModel, navController: NavController) {
    val novedadesRecibidas by viewModel.novedadesRecibidas.collectAsState()
    var novedadSeleccionada by remember { mutableStateOf<NovedadEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novedades Recibidas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (novedadesRecibidas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay novedades reportadas recibidas",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(novedadesRecibidas, key = { it.id }) { novedad ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { novedadSeleccionada = novedad },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tipo: ${novedad.tipo}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val fechaTexto = if (novedad.fechaFin != null) {
                                        "${novedad.fecha} al ${novedad.fechaFin}"
                                    } else {
                                        novedad.fecha.toString()
                                    }
                                    Text(
                                        text = fechaTexto,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = novedad.motivo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Reportado por: ${novedad.userId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            novedadSeleccionada?.let { novedad ->
                AlertDialog(
                    onDismissRequest = { novedadSeleccionada = null },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AssignmentLate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Detalle de Novedad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Tipo: ${novedad.tipo}", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            val duracionTexto = if (novedad.fechaFin != null) {
                                "Rango: Del ${novedad.fecha} al ${novedad.fechaFin}"
                            } else {
                                "Fecha: ${novedad.fecha}"
                            }
                            Text(text = duracionTexto, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Motivo / Explicación:", fontWeight = FontWeight.SemiBold)
                            Text(text = novedad.motivo, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Reportado por ID: ${novedad.userId}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { novedadSeleccionada = null }) {
                            Text("CERRAR")
                        }
                    }
                )
            }
        }
    }
}
