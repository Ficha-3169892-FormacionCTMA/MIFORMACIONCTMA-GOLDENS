package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificadosScreen(viewModel: AppViewModel, navController: NavController) {
    val certificados by viewModel.certificados.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Certificados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (certificados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No tienes certificados disponibles", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(certificados) { cert ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        ListItem(
                            headlineContent = { Text(cert.nombre, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Emitido el: ${cert.fechaEmision}") },
                            overlineContent = { Text(cert.tipo) },
                            trailingContent = {
                                IconButton(onClick = { /* Implement real download logic */ }) {
                                    Icon(Icons.Default.Download, contentDescription = "Descargar", tint = Color(0xFF39A900))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
