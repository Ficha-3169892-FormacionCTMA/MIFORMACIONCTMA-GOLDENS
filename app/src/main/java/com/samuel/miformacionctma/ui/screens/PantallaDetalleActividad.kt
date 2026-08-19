package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.estadoActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleActividad(
    actividad: ActividadFormativa?,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (actividad == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Actividad no encontrada")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                DetailItem(label = "Estado", value = estadoActividad(actividad))
                DetailItem(label = "Prioridad", value = actividad.prioridad.name)
                DetailItem(label = "Progreso", value = "${actividad.progreso}%")
                DetailItem(label = "Inicio", value = actividad.fechaInicio.toString())
                DetailItem(label = "Fin", value = actividad.fechaFin.toString())
                DetailItem(label = "Días restantes", value = actividad.diasRestantes.toString())

                if (!actividad.descripcion.isNullOrBlank()) {
                    Divider()
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
