package com.samuel.miformacionctma.ui.bitacora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitacoraScreen(viewModel: BitacoraViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvento(BitacoraEvento.LimpiarMensaje)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Bitácora de Seguimiento") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Formulario
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.onEvento(BitacoraEvento.TituloCambiado(it)) },
                label = { Text("Título de la actividad") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.any { it.contains("título", ignoreCase = true) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = uiState.contenido,
                onValueChange = { viewModel.onEvento(BitacoraEvento.ContenidoCambiado(it)) },
                label = { Text("Contenido / Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                isError = uiState.errores.any { it.contains("contenido", ignoreCase = true) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.horas,
                onValueChange = { viewModel.onEvento(BitacoraEvento.HorasCambiadas(it)) },
                label = { Text("Horas dedicadas") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.any { it.contains("horas", ignoreCase = true) }
            )

            if (uiState.errores.isNotEmpty()) {
                uiState.errores.forEach { error ->
                    Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onEvento(BitacoraEvento.GuardarBitacora) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Guardar Bitácora")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp))

            Text("Registros Anteriores", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.listaBitacoras) { bitacora ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = bitacora.titulo, style = MaterialTheme.typography.headlineSmall)
                            Text(text = "Fecha: ${bitacora.fecha} | Horas: ${bitacora.horas}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = bitacora.contenido)
                        }
                    }
                }
            }
        }
    }
}
