package com.samuel.miformacionctma.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    val progresoSeguro = actividad.progreso.coerceIn(0, 100)

    val estado = when {
        progresoSeguro >= 100 -> "Completada"
        progresoSeguro <= 0 -> "Pendiente"
        else -> "En proceso"
    }

    val descripcionAccesible = buildString {
        append("Actividad ${actividad.titulo}. ")
        append("Estado: $estado. ")
        append("Progreso: $progresoSeguro por ciento. ")
        append("Prioridad: ${actividad.prioridad.name.lowercase()}. ")
        append("Días restantes: ${actividad.diasRestantes}.")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = descripcionAccesible
                role = Role.Button
            }
            .clickable {
                onClick(actividad)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Título
            Text(
                text = actividad.titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            actividad.descripcion?.let { descripcion ->
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Estado y prioridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Estado: $estado",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = when (actividad.prioridad) {
                        Prioridad.BAJA -> "🟢 Prioridad: Baja"
                        Prioridad.MEDIA -> "🟡 Prioridad: Media"
                        Prioridad.ALTA -> "🔴 Prioridad: Alta"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progreso
            Text(
                text = "Progreso: $progresoSeguro%",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progresoSeguro / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Días restantes
            Text(
                text = if (actividad.diasRestantes >= 0) {
                    "Días restantes: ${actividad.diasRestantes}"
                } else {
                    "Actividad vencida"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TarjetaActividadPreview() {
    MiFormacionCTMATheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 1L,
                titulo = "Diseño de interfaz de usuario para Mi Formación CTMA",
                descripcion = "Actividad de prueba con un título largo para comprobar que la tarjeta no recorte el contenido.",
                progreso = 80,
                diasRestantes = 2,
                prioridad = Prioridad.ALTA
            ),
            onClick = {}
        )
    }
}
@Preview(
    showBackground = true
)
@Composable
fun TarjetaActividadCompletadaPreview() {
    MiFormacionCTMATheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 2L,
                titulo = "Actividad completada",
                descripcion = "Esta actividad representa el límite superior del progreso.",
                progreso = 100,
                diasRestantes = 0,
                prioridad = Prioridad.MEDIA
            ),
            onClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun TarjetaActividadLimitesPreview() {
    MiFormacionCTMATheme {
        TarjetaActividad(
            actividad = ActividadFormativa(
                id = 99L,
                titulo = "Prueba de valores límite",
                descripcion = null,
                progreso = 150,
                diasRestantes = -2,
                prioridad = Prioridad.ALTA
            ),
            onClick = {}
        )
    }
}