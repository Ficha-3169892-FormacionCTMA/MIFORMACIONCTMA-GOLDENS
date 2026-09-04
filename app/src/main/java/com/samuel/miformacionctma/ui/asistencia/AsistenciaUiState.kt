package com.samuel.miformacionctma.ui.asistencia

import com.samuel.miformacionctma.data.local.entities.AsistenciaEntity

data class AsistenciaUiState(
    val isLoading: Boolean = false,
    val asistencias: List<AsistenciaEntity> = emptyList(),
    val error: String? = null,
    val mensajeExito: String? = null,
    val isRegistrando: Boolean = false
)
