package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.components.EncabezadoFormacion
import com.samuel.miformacionctma.ui.components.TarjetaActividad
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme
import java.time.LocalDate

@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EncabezadoFormacion(
                nombreAprendiz = "Samuel", // En semana 4 vendrá de un estado real
                resumen = if (actividades.isEmpty()) 
                    "No hay actividades" 
                else 
                    "Tienes ${actividades.size} actividades programadas"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (actividades.isEmpty()) {
                EstadoVacio(
                    mensaje = "Parece que no tienes actividades registradas por ahora.",
                    modifier = Modifier.align(Alignment.Center)
                )
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
fun ContenidoAdaptable(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Usamos this.maxWidth para asegurar que el scope se considere utilizado
        if (this.maxWidth < 600.dp) {
            // Vista compacta: Lista simple
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = actividades,
                    key = { it.id }
                ) { actividad ->
                    TarjetaActividad(
                        actividad = actividad,
                        onActividadClick = onActividadClick
                    )
                }
            }
        } else {
            // Vista ampliada: Cuadrícula de 2 columnas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = actividades,
                    key = { it.id }
                ) { actividad ->
                    TarjetaActividad(
                        actividad = actividad,
                        onActividadClick = onActividadClick
                    )
                }
            }
        }
    }
}

@Composable
fun EstadoVacio(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null, // Decorativo
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PantallaActividadesCompactaPreview() {
    MiFormacionCTMATheme {
        PantallaActividades(
            actividades = actividadesSimuladas,
            onActividadClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun PantallaActividadesAmpliadaPreview() {
    MiFormacionCTMATheme {
        PantallaActividades(
            actividades = actividadesSimuladas,
            onActividadClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaActividadesVaciaPreview() {
    MiFormacionCTMATheme {
        PantallaActividades(
            actividades = emptyList(),
            onActividadClick = {}
        )
    }
}

private val hoy = LocalDate.now()

val actividadesSimuladas = listOf(
    ActividadFormativa(1L, "Introducción a Kotlin", "Fundamentos", hoy, hoy.plusDays(7), 100, 0, Prioridad.ALTA),
    ActividadFormativa(2L, "Jetpack Compose Basics", "Layouts", hoy, hoy.plusDays(7), 50, 2, Prioridad.MEDIA),
    ActividadFormativa(3L, "Material 3 Design", "Temas y Colores", hoy, hoy.plusDays(7), 80, 1, Prioridad.ALTA),
    ActividadFormativa(4L, "Navegación", "Setup de NavHost", hoy, hoy.plusDays(7), 0, 5, Prioridad.BAJA),
    ActividadFormativa(5L, "Persistencia con Room", "Bases de datos", hoy, hoy.plusDays(7), 10, 10, Prioridad.MEDIA),
    ActividadFormativa(6L, "Retrofit y API", "Networking", hoy, hoy.plusDays(7), 0, 15, Prioridad.ALTA),
    ActividadFormativa(7L, "ViewModel y LiveData", "State management", hoy, hoy.plusDays(7), 0, 20, Prioridad.MEDIA),
    ActividadFormativa(8L, "Unit Testing", "JUnit", hoy, hoy.plusDays(7), 0, 30, Prioridad.BAJA),
    ActividadFormativa(9L, "UI Testing", "Espresso", hoy, hoy.plusDays(7), 0, 35, Prioridad.BAJA),
    ActividadFormativa(10L, "Dagger Hilt", "Dependency Injection", hoy, hoy.plusDays(7), 0, 40, Prioridad.ALTA)
)
