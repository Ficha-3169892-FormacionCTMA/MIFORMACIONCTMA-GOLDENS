package com.samuel.miformacionctma.ui

data class DashboardData(
    val totalActividades: Int = 0,
    val completadas: Int = 0,
    val enProceso: Int = 0,
    val pendientes: Int = 0,
    val vencidas: Int = 0,
    val fallidas: Int = 0,
    val enRevision: Int = 0,
    val progresoGeneral: Int = 0
)
