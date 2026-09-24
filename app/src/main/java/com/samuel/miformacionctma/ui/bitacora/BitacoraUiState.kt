package com.samuel.miformacionctma.ui.bitacora

import com.samuel.miformacionctma.model.Bitacora

data class BitacoraUiState(
    val titulo: String = "",
    val contenido: String = "",
    val horas: String = "",
    val isLoading: Boolean = false,
    val mensajeExito: String? = null,
    val errores: List<String> = emptyList(),
    val listaBitacoras: List<Bitacora> = emptyList()
) {
    val esValido: Boolean = titulo.isNotBlank() && contenido.length >= 10 && horas.toIntOrNull() != null && horas.toInt() > 0
}
