package com.samuel.miformacionctma.ui.screens

import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import java.time.LocalDate

fun obtenerActividadesDePrueba(): List<ActividadFormativa> = listOf(
    ActividadFormativa(
        id = 1L,
        titulo = "Introducción a Kotlin",
        descripcion = "Repasar sintaxis básica, variables y tipos.",
        fechaInicio = LocalDate.now().minusDays(10),
        fechaFin = LocalDate.now().minusDays(5),
        progreso = 100,
        diasRestantes = 0,
        prioridad = Prioridad.MEDIA
    ),
    ActividadFormativa(
        id = 2L,
        titulo = "Variables y tipos de datos",
        descripcion = "Practicar el uso de val, var y tipos de datos.",
        fechaInicio = LocalDate.now().minusDays(5),
        fechaFin = LocalDate.now().plusDays(1),
        progreso = 90,
        diasRestantes = 1,
        prioridad = Prioridad.ALTA
    ),
    ActividadFormativa(
        id = 3L,
        titulo = "Funciones en Kotlin",
        descripcion = "Crear funciones reutilizables y fáciles de probar.",
        fechaInicio = LocalDate.now().minusDays(4),
        fechaFin = LocalDate.now().plusDays(2),
        progreso = 80,
        diasRestantes = 2,
        prioridad = Prioridad.MEDIA
    ),
    ActividadFormativa(
        id = 4L,
        titulo = "Null safety",
        descripcion = "Aplicar ?, ?. y evitar el uso innecesario de !!.",
        fechaInicio = LocalDate.now().minusDays(3),
        fechaFin = LocalDate.now().plusDays(3),
        progreso = 70,
        diasRestantes = 3,
        prioridad = Prioridad.ALTA
    ),
    ActividadFormativa(
        id = 5L,
        titulo = "Colecciones e iteradores",
        descripcion = "Uso de List, Map, Set y transformaciones con map/filter.",
        fechaInicio = LocalDate.now().minusDays(2),
        fechaFin = LocalDate.now().plusDays(4),
        progreso = 60,
        diasRestantes = 4,
        prioridad = Prioridad.BAJA
    ),
    ActividadFormativa(
        id = 6L,
        titulo = "Fundamentos de Jetpack Compose",
        descripcion = "Construcción de layouts básicos con Column, Row y Box.",
        fechaInicio = LocalDate.now().minusDays(1),
        fechaFin = LocalDate.now().plusDays(5),
        progreso = 55,
        diasRestantes = 5,
        prioridad = Prioridad.ALTA
    ),
    ActividadFormativa(
        id = 7L,
        titulo = "Gestión de Estado (State)",
        descripcion = "Uso de remember, mutableStateOf y hoisting de estado.",
        fechaInicio = LocalDate.now(),
        fechaFin = LocalDate.now().plusDays(6),
        progreso = 40,
        diasRestantes = 6,
        prioridad = Prioridad.MEDIA
    ),
    ActividadFormativa(
        id = 8L,
        titulo = "Listas adaptables con LazyColumn",
        descripcion = "Optimización de listas grandes y cuadrículas dinámicas.",
        fechaInicio = LocalDate.now(),
        fechaFin = LocalDate.now().plusDays(7),
        progreso = 30,
        diasRestantes = 7,
        prioridad = Prioridad.BAJA
    ),
    ActividadFormativa(
        id = 9L,
        titulo = "Navegación en Compose",
        descripcion = "Implementación de NavHost y envío de parámetros.",
        fechaInicio = LocalDate.now(),
        fechaFin = LocalDate.now().plusDays(8),
        progreso = 15,
        diasRestantes = 8,
        prioridad = Prioridad.MEDIA
    ),
    ActividadFormativa(
        id = 10L,
        titulo = "Arquitectura MVVM y Corrutinas",
        descripcion = "Integración de ViewModels y llamadas asíncronas.",
        fechaInicio = LocalDate.now(),
        fechaFin = LocalDate.now().plusDays(10),
        progreso = 0,
        diasRestantes = 10,
        prioridad = Prioridad.ALTA
    )
)