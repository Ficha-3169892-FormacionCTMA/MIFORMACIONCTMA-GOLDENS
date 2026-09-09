package com.samuel.miformacionctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividad(
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (actividad == null) {
            Text(
                text = "Actividad no encontrada.",
                modifier = Modifier.padding(paddingValues).padding(16.dp)
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = actividad.titulo, style = MaterialTheme.typography.headlineMedium)
                
                PrioridadBadge(actividad.prioridad)
                
                HorizontalDivider()
                
                Text(text = "Descripción", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = actividad.descripcion ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Inicio", style = MaterialTheme.typography.titleSmall)
                        Text(text = actividad.fechaInicio.toString())
                    }
                    Column {
                        Text(text = "Fin", style = MaterialTheme.typography.titleSmall)
                        Text(text = actividad.fechaFin.toString())
                    }
                }
                
                Column {
                    Text(text = "Progreso actual", style = MaterialTheme.typography.titleSmall)
                    Text(text = "${actividad.progreso}%", style = MaterialTheme.typography.bodyLarge)
                }

                Text(
                    text = "Días restantes: ${actividad.diasRestantes}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}