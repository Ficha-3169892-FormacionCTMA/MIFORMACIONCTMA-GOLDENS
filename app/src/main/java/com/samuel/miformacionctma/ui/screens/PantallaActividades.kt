package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.ui.components.ContenidoAdaptable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    modifier: Modifier = Modifier,
    onActividadClick: (ActividadFormativa) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Formación CTMA") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (actividades.isEmpty()) {
                EstadoVacio()
            } else {
                ContenidoAdaptable(
                    actividades = actividades,
                    onActividadClick = onActividadClick
                )
            }
        }
    }
}

@Composable
private fun EstadoVacio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay actividades registradas",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Las nuevas tareas asignadas aparecerán en este lugar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Previews
@Preview(name = "Pantalla Estrecha", widthDp = 360, heightDp = 640)
@Composable
fun PantallaActividadesEstrechaPreview() {
    MaterialTheme {
        PantallaActividades(actividades = obtenerActividadesDePrueba())
    }
}

@Preview(name = "Pantalla Ancha", widthDp = 700, heightDp = 500)
@Composable
fun PantallaActividadesAnchaPreview() {
    MaterialTheme {
        PantallaActividades(actividades = obtenerActividadesDePrueba())
    }
}