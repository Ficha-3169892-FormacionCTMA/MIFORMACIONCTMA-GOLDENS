package com.samuel.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.model.actividadesUrgentes
import com.samuel.miformacionctma.model.promedioProgreso
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val actividades = listOf(
            ActividadFormativa(
                id = 1,
                titulo = "Diseño de interfaz",
                descripcion = "Crear la pantalla principal",
                progreso = 80,
                diasRestantes = 2,
                prioridad = Prioridad.ALTA
            ),
            ActividadFormativa(
                id = 2,
                titulo = "Implementar reglas",
                descripcion = "Crear funciones de negocio",
                progreso = 50,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            ),
            ActividadFormativa(
                id = 3,
                titulo = "Entrega README",
                descripcion = "Completar documentación",
                progreso = 100,
                diasRestantes = 0,
                prioridad = Prioridad.BAJA
            )
        )

        val promedio = promedioProgreso(actividades)
        val urgentes = actividadesUrgentes(actividades)

        val resumen = """
            Mi Formación CTMA
            
            Actividades registradas: ${actividades.size}
            Promedio de progreso: ${"%.1f".format(promedio)}%
            Actividades urgentes: ${urgentes.size}
        """.trimIndent()

        setContent {
            MiFormacionCTMATheme {
                PantallaInicio(resumen)
            }
        }
    }
}

@Composable
fun PantallaInicio(resumen: String) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Mi Formación CTMA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resumen,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {

    MiFormacionCTMATheme {
        PantallaInicio(
            """
            Mi Formación CTMA
            
            Actividades registradas: 3
            Promedio de progreso: 76.7%
            Actividades urgentes: 1
            """.trimIndent()
        )
    }
}