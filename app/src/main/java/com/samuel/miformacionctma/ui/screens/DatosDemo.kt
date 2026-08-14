package com.samuel.miformacionctma.ui.screens

import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.EstadoActividadEnum
import com.samuel.miformacionctma.model.Prioridad

fun obtenerActividadesDePrueba(): List<ActividadFormativa> = listOf(
    ActividadFormativa(
        id = 1,
        titulo = "Introducción a Kotlin",
        descripcion = "Repasar sintaxis básica, variables y tipos.",
        progreso = 100,
        diasRestantes = 0,
        prioridad = Prioridad.MEDIA,
        estado = EstadoActividadEnum.ENTREGADA,
        urlEvidencia = "https://github.com/paboncito666/GuiaAndroid1.git"
    ),
    ActividadFormativa(
        id = 2,
        titulo = "Variables y tipos de datos",
        descripcion = "Practicar el uso de val, var y tipos de datos.",
        progreso = 90,
        diasRestantes = 1,
        prioridad = Prioridad.ALTA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 3,
        titulo = "Funciones en Kotlin",
        descripcion = "Crear funciones reutilizables y fáciles de probar.",
        progreso = 80,
        diasRestantes = 2,
        prioridad = Prioridad.MEDIA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 4,
        titulo = "Null safety",
        descripcion = "Aplicar ?, ?. y evitar el uso innecesario de !!.",
        progreso = 70,
        diasRestantes = 3,
        prioridad = Prioridad.ALTA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 5,
        titulo = "Colecciones e iteradores",
        descripcion = "Uso de List, Map, Set y transformaciones con map/filter.",
        progreso = 60,
        diasRestantes = 4,
        prioridad = Prioridad.BAJA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 6,
        titulo = "Fundamentos de Jetpack Compose",
        descripcion = "Construcción de layouts básicos con Column, Row y Box.",
        progreso = 55,
        diasRestantes = 5,
        prioridad = Prioridad.ALTA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 7,
        titulo = "Gestión de Estado (State)",
        descripcion = "Uso de remember, mutableStateOf y hoisting de estado.",
        progreso = 40,
        diasRestantes = 6,
        prioridad = Prioridad.MEDIA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 8,
        titulo = "Listas adaptables con LazyColumn",
        descripcion = "Optimización de listas grandes y cuadrículas dinámicas.",
        progreso = 30,
        diasRestantes = 7,
        prioridad = Prioridad.BAJA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 9,
        titulo = "Navegación en Compose",
        descripcion = "Implementación de NavHost y envío de parámetros.",
        progreso = 15,
        diasRestantes = 8,
        prioridad = Prioridad.MEDIA,
        estado = EstadoActividadEnum.EN_PROCESO
    ),
    ActividadFormativa(
        id = 10,
        titulo = "Arquitectura MVVM y Corrutinas",
        descripcion = "Integración de ViewModels y llamadas asíncronas.",
        progreso = 0,
        diasRestantes = 10,
        prioridad = Prioridad.ALTA,
        estado = EstadoActividadEnum.PENDIENTE
    )
)