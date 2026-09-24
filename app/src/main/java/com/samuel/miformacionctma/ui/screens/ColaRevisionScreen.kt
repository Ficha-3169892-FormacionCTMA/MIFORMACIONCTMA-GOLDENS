package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.RevisionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaRevisionScreen(
    appViewModel: AppViewModel,
    revisionViewModel: RevisionViewModel,
    navController: NavController
) {
    val actividades by appViewModel.actividades.collectAsStateWithLifecycle()
    val currentUserId by appViewModel.userId.collectAsStateWithLifecycle()
    val conteosEnRevision by revisionViewModel.conteosEnRevision.collectAsStateWithLifecycle()

    val actividadesInstructor = remember(actividades, currentUserId) {
        actividades.filter { it.instructorId == currentUserId && it.remoteId != null }
    }

    LaunchedEffect(currentUserId) {
        currentUserId?.let { revisionViewModel.cargarConteosEnRevision(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cola de Revisión") },
                actions = {
                    IconButton(onClick = {
                        currentUserId?.let { revisionViewModel.cargarConteosEnRevision(it) }
                    }) {
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
            if (actividadesInstructor.isEmpty()) {
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
                        text = "No tienes actividades creadas todavía",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(actividadesInstructor, key = { it.id }) { actividad ->
                        val pendientesRevision = conteosEnRevision[actividad.remoteId] ?: 0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    actividad.remoteId?.let { remoteId ->
                                        navController.navigate("revision/$remoteId")
                                    }
                                },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = actividad.titulo,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Hasta: ${actividad.fechaFin}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Badge(
                                    containerColor = if (pendientesRevision > 0) Color(0xFFF9A825) else Color.Gray
                                ) {
                                    Text(
                                        text = "$pendientesRevision por revisar",
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
