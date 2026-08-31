package com.samuel.miformacionctma.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

@Composable
fun EncabezadoFormacion(
    nombreAprendiz: String,
    resumen: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bienvenido, $nombreAprendiz",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = resumen,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "Vista Normal")
@Composable
fun PreviewEncabezadoNormal() {
    MiFormacionCTMATheme {
        EncabezadoFormacion(
            nombreAprendiz = "Samuel",
            resumen = "Tienes 5 actividades pendientes para hoy."
        )
    }
}

@Preview(showBackground = true, name = "Vista Fuente Grande", fontScale = 1.5f)
@Composable
fun PreviewEncabezadoGrande() {
    MiFormacionCTMATheme {
        EncabezadoFormacion(
            nombreAprendiz = "Samuel",
            resumen = "Tienes 5 actividades pendientes para hoy."
        )
    }
}

@Preview(showBackground = true, name = "Vista Ancho Ampliado", widthDp = 600)
@Composable
fun PreviewEncabezadoAmpliado() {
    MiFormacionCTMATheme {
        EncabezadoFormacion(
            nombreAprendiz = "Samuel",
            resumen = "Tienes 5 actividades pendientes para hoy. El progreso general es del 60%."
        )
    }
}