package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.entregarActividad
import com.samuel.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    modifier: Modifier = Modifier
) {
    var listaActividades by remember { mutableStateOf(actividades) }
    var actividadSeleccionada by remember { mutableStateOf<ActividadFormativa?>(null) }
    var textoUrl by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Formación CTMA - Actividades") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = listaActividades,
                key = { it.id }
            ) { actividad ->
                TarjetaActividad(
                    actividad = actividad,
                    onClick = { seleccionada ->
                        actividadSeleccionada = seleccionada
                        textoUrl = seleccionada.urlEvidencia ?: ""
                        mensajeError = null
                    }
                )
            }
        }

        // Diálogo de confirmación de entrega (Laboratorio de UI)
        actividadSeleccionada?.let { actividad ->
            AlertDialog(
                onDismissRequest = { actividadSeleccionada = null },
                title = { Text("Entregar Evidencia") },
                text = {
                    Column {
                        Text(text = "Actividad: ${actividad.titulo}")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = textoUrl,
                            onValueChange = {
                                textoUrl = it
                                mensajeError = null
                            },
                            label = { Text("URL de Evidencia (GitHub / Drive)") },
                            isError = mensajeError != null,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        mensajeError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val resultado = entregarActividad(actividad, textoUrl)
                            resultado.onSuccess { actualizada ->
                                listaActividades = listaActividades.map {
                                    if (it.id == actualizada.id) actualizada else it
                                }
                                actividadSeleccionada = null
                            }.onFailure { ex ->
                                mensajeError = ex.message
                            }
                        }
                    ) {
                        Text("Confirmar Entrega")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { actividadSeleccionada = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}