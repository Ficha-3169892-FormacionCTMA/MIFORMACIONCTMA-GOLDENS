package com.samuel.miformacionctma.ui.formulario

import java.time.LocalDate

data class FormularioActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val progreso: String = "0"
) {

    val tituloError: String?
        get() = when {
            titulo.trim().length < 3 -> "El título debe tener al menos 3 caracteres."
            titulo.trim().length > 80 -> "El título no puede superar 80 caracteres."
            else -> null
        }

    val descripcionError: String?
        get() = if (descripcion.length > 240) {
            "La descripción no puede superar 240 caracteres."
        } else {
            null
        }

    val progresoError: String?
        get() {
            val valor = progreso.toIntOrNull()
            return when {
                valor == null -> "Indica un número entero."
                valor !in 0..100 -> "El progreso debe estar entre 0 y 100."
                else -> null
            }
        }

    val fechasError: String?
        get() = try {
            val inicio = LocalDate.parse(fechaInicio)
            val fin = LocalDate.parse(fechaFin)
            val hoy = LocalDate.now()
            
            when {
                inicio.isBefore(hoy) -> "La fecha de inicio no puede ser anterior a hoy."
                fin.isBefore(inicio) -> "La fecha de fin no puede ser anterior a la de inicio."
                else -> null
            }
        } catch (_: Exception) {
            "Formato inválido (yyyy-MM-dd)."
        }

    val esValido: Boolean
        get() = tituloError == null &&
                descripcionError == null &&
                progresoError == null &&
                fechasError == null
}