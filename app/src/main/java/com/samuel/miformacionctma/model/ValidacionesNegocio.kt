package com.samuel.miformacionctma.model

import java.time.LocalDate

object ValidacionesNegocio {

    fun validarBitacora(titulo: String, contenido: String, horas: Int): List<String> {
        val errores = mutableListOf<String>()
        if (titulo.isBlank()) errores.add("El título de la bitácora es obligatorio.")
        if (contenido.length < 10) errores.add("El contenido debe tener al menos 10 caracteres.")
        if (horas <= 0) errores.add("Las horas deben ser mayores a 0.")
        return errores
    }

    fun validarEvidencia(nombreArchivo: String, url: String): List<String> {
        val errores = mutableListOf<String>()
        if (nombreArchivo.isBlank()) errores.add("El nombre del archivo es obligatorio.")
        if (!url.startsWith("http")) errores.add("La URL de la evidencia debe ser válida.")
        return errores
    }

    fun validarAsistencia(fecha: LocalDate): String? {
        return if (fecha.isAfter(LocalDate.now())) {
            "No se puede registrar asistencia para una fecha futura."
        } else null
    }
}
