package com.samuel.miformacionctma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.model.estadoActividad
import java.time.LocalDate

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    val estadoTexto = estadoActividad(actividad)
    val textoDias = textoDiasRestantes(actividad.diasRestantes)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .semantics {
                // Configuración de accesibilidad para lectores de pantalla (TalkBack)
                contentDescription = "Actividad ${actividad.titulo}, Estado $estadoTexto, " +
                        "Progreso ${actividad.progreso} por ciento, $textoDias, " +
                        "Prioridad ${actividad.prioridad.name}"
            },
        onClick = { onClick(actividad) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Título y Chip de Prioridad
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(text = actividad.prioridad.name) }
                )
            }

            // Descripción (si existe)
            if (!actividad.descripcion.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = actividad.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Días restantes y Estado
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = textoDias,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = estadoTexto,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Fechas de la actividad
            Text(
                text = "${actividad.fechaInicio} - ${actividad.fechaFin}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Indicador de Progreso Límite
            LinearProgressIndicator(
                progress = { (actividad.progreso / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
    }
}

private fun textoDiasRestantes(dias: Int): String = when {
    dias < 0 -> "Vencida hace ${-dias} día(s)"
    dias == 0 -> "Vence hoy"
    else -> "Faltan $dias día(s)"
}

// -----------------------------------------------------------
// PREVIEWS
// -----------------------------------------------------------

@Preview(name = "Normal", showBackground = true)
@Composable
fun TarjetaActividadNormalPreview() {
    MaterialTheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 1L,
                titulo = "Guía 3 Jetpack Compose",
                descripcion = "Desarrollo de interfaz declarativa",
                fechaInicio = LocalDate.now(),
                fechaFin = LocalDate.now().plusDays(7),
                progreso = 50,
                diasRestantes = 3,
                prioridad = Prioridad.ALTA
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Título Largo y Progreso Límite 100%", showBackground = true)
@Composable
fun TarjetaActividadLargaPreview() {
    MaterialTheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 2L,
                titulo = "Construcción de componentes muy complejos para la interfaz adaptable de la aplicación móvil de formación SENA CTMA",
                descripcion = "Esta descripción es bastante extensa para verificar que el componente se ajuste bien sin desbordar la pantalla ni recortar texto inadecuadamente.",
                fechaInicio = LocalDate.now().minusDays(5),
                fechaFin = LocalDate.now(),
                progreso = 100,
                diasRestantes = 0,
                prioridad = Prioridad.MEDIA
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Fuente Grande (Accesibilidad)", fontScale = 1.5f, showBackground = true)
@Composable
fun TarjetaActividadFuenteGrandePreview() {
    MaterialTheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 3L,
                titulo = "Prueba de Fuente Accesible",
                descripcion = "Verificación con escalado de letra al 150%",
                fechaInicio = LocalDate.now(),
                fechaFin = LocalDate.now().plusDays(10),
                progreso = 0,
                diasRestantes = -2,
                prioridad = Prioridad.ALTA
            ),
            onClick = {}
        )
    }
}