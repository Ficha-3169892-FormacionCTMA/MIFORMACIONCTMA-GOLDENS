package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.samuel.miformacionctma.data.repository.EntregaParaRevision
import com.samuel.miformacionctma.ui.RevisionUiState
import com.samuel.miformacionctma.ui.RevisionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionEvidenciasScreen(
    actividadRemoteId: Long,
    viewModel: RevisionViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var fotoAmpliada by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(actividadRemoteId) {
        viewModel.cargarEntregas(actividadRemoteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisión de Entregas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarEntregas(actividadRemoteId) }) {
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
            when (val state = uiState) {
                is RevisionUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RevisionUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.mensaje, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarEntregas(actividadRemoteId) }) {
                            Text("Reintentar")
                        }
                    }
                }
                is RevisionUiState.Contenido -> {
                    if (state.lista.isEmpty()) {
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
                                text = "Nadie ha entregado evidencia todavía",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.lista, key = { it.asignacionId }) { entrega ->
                                EntregaRevisionItem(
                                    entrega = entrega,
                                    onAprobar = {
                                        viewModel.marcarEstado(
                                            actividadRemoteId = actividadRemoteId,
                                            asignacionId = entrega.asignacionId,
                                            nuevoEstado = "completada"
                                        )
                                    },
                                    onRechazar = {
                                        viewModel.marcarEstado(
                                            actividadRemoteId = actividadRemoteId,
                                            asignacionId = entrega.asignacionId,
                                            nuevoEstado = "fallida"
                                        )
                                    },
                                    onRevertir = {
                                        viewModel.marcarEstado(
                                            actividadRemoteId = actividadRemoteId,
                                            asignacionId = entrega.asignacionId,
                                            nuevoEstado = "pendiente"
                                        )
                                    },
                                    onVerFoto = { fotoUrl ->
                                        fotoAmpliada = fotoUrl
                                    }
                                )
                            }
                        }
                    }
                }
            }

            fotoAmpliada?.let { url ->
                Dialog(
                    onDismissRequest = { fotoAmpliada = null },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = url,
                                contentDescription = "Foto ampliada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            IconButton(
                                onClick = { fotoAmpliada = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EntregaRevisionItem(
    entrega: EntregaParaRevision,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit,
    onRevertir: () -> Unit,
    onVerFoto: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entrega.aprendizNombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entrega.aprendizCorreo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                val (statusText, badgeColor) = when (entrega.estado) {
                    "completada" -> "Completada" to Color(0xFF2E7D32)
                    "fallida" -> "Fallida" to Color.Red
                    "en_revision" -> "En revisión" to Color(0xFFF9A825)
                    else -> "Pendiente" to Color.Gray
                }

                Badge(containerColor = badgeColor) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!entrega.fotoUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onVerFoto?.invoke(entrega.fotoUrl) }
                ) {
                    AsyncImage(
                        model = entrega.fotoUrl,
                        contentDescription = "Foto de evidencia del aprendiz",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!entrega.comentario.isNullOrBlank()) {
                Text(
                    text = "Comentario del Aprendiz:",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = entrega.comentario,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Sin comentario adjunto",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (!entrega.fechaEntrega.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Entregado el: ${entrega.fechaEntrega.take(16).replace("T", " ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (entrega.estado == "completada" || entrega.estado == "fallida") {
                OutlinedButton(
                    onClick = onRevertir,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Revertir a pendiente")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAprobar,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Aprobar")
                    }

                    Button(
                        onClick = onRechazar,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar")
                    }
                }
            }
        }
    }
}
