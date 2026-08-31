package com.samuel.miformacionctma.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onActividadClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    val progreso = actividad.progreso.coerceIn(0, 100)
    val estado = when {
        progreso >= 100 -> "Completada"
        progreso > 0 -> "En proceso"
        else -> "Pendiente"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Actividad: ${actividad.titulo}, Estado: $estado, Progreso: $progreso por ciento"
                role = Role.Button
            }
            .clickable(
                onClick = { onActividadClick(actividad) },
                onClickLabel = "Ver detalles de la actividad"
            ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fecha: ${if (actividad.diasRestantes >= 0) "En ${actividad.diasRestantes} días" else "Vencida"}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progreso / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )
                Text(
                    text = "$progreso%",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Actividad Normal")
@Composable
fun PreviewTarjetaNormal() {
    MiFormacionCTMATheme {
        TarjetaActividad(
            actividad = ActividadFormativa(1, "Taller de Kotlin", "Contenido básico", 45, 3, Prioridad.MEDIA),
            onActividadClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Título Largo")
@Composable
fun PreviewTarjetaTituloLargo() {
    MiFormacionCTMATheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 2,
                titulo = "Investigación sobre Arquitectura de Software Limpia y Patrones de Diseño Avanzados en Aplicaciones Android Modernas",
                descripcion = null,
                progreso = 10,
                diasRestantes = 7,
                prioridad = Prioridad.ALTA
            ),
            onActividadClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Límites de Progreso")
@Composable
fun PreviewTarjetaLimites() {
    MiFormacionCTMATheme {
        Column(Modifier.padding(8.dp)) {
            TarjetaActividad(
                actividad = ActividadFormativa(3, "Pendiente", null, 0, 10, Prioridad.BAJA),
                onActividadClick = {}
            )
            TarjetaActividad(
                actividad = ActividadFormativa(4, "Completada", null, 100, 0, Prioridad.MEDIA),
                onActividadClick = {}
            )
        }
    }
}