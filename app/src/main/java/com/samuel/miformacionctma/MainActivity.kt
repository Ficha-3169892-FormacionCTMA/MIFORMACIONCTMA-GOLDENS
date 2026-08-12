package com.samuel.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.screens.PantallaActividades
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MiFormacionCTMATheme {

                PantallaActividades(
                    actividades = actividadesEjemplo,
                    onActividadClick = { actividad ->
                        println("Actividad seleccionada: ${actividad.titulo}")
                    }
                )
            }
        }
    }
}

private val actividadesEjemplo = listOf(

    ActividadFormativa(
        id = 1L,
        titulo = "Introducción a Kotlin",
        descripcion = "Repasar sintaxis básica, variables y tipos.",
        progreso = 100,
        diasRestantes = 0,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 2L,
        titulo = "Variables y tipos de datos",
        descripcion = "Practicar el uso de val, var y tipos de datos.",
        progreso = 90,
        diasRestantes = 1,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 3L,
        titulo = "Funciones en Kotlin",
        descripcion = "Crear funciones reutilizables y fáciles de probar.",
        progreso = 80,
        diasRestantes = 2,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 4L,
        titulo = "Null safety",
        descripcion = "Aplicar ?, ?: y evitar el uso innecesario de !!.",
        progreso = 70,
        diasRestantes = 3,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 5L,
        titulo = "Colecciones",
        descripcion = "Trabajar con listas y operaciones sobre colecciones.",
        progreso = 65,
        diasRestantes = 4,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 6L,
        titulo = "Data classes",
        descripcion = "Modelar información utilizando data class.",
        progreso = 60,
        diasRestantes = 5,
        prioridad = Prioridad.BAJA
    ),

    ActividadFormativa(
        id = 7L,
        titulo = "Reglas de negocio",
        descripcion = "Implementar reglas verificables para las actividades.",
        progreso = 50,
        diasRestantes = 6,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 8L,
        titulo = "Diseño de componentes Compose",
        descripcion = "Crear componentes reutilizables con Jetpack Compose.",
        progreso = 40,
        diasRestantes = 7,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 9L,
        titulo = "Lista de actividades",
        descripcion = "Construir una lista utilizando LazyColumn y claves estables.",
        progreso = 30,
        diasRestantes = 8,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 10L,
        titulo = "Integración y sustentación",
        descripcion = "Integrar los componentes y preparar la demostración.",
        progreso = 10,
        diasRestantes = 10,
        prioridad = Prioridad.MEDIA
    )
)