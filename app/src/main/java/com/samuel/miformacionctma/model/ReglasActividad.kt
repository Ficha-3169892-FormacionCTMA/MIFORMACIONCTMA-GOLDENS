package com.samuel.miformacionctma.model

fun validarActividad(
    titulo: String,
    progreso: Int
): List<String> {

    val errores = mutableListOf<String>()

    if (titulo.isBlank())
        errores.add("El título es obligatorio")

    if (progreso !in 0..100)
        errores.add("El progreso debe estar entre 0 y 100")

    return errores
}

fun estadoActividad(
    actividad: ActividadFormativa
): String {

    return when {

        actividad.progreso == 100 ->
            "COMPLETADA"

        actividad.diasRestantes < 0 ->
            "VENCIDA"

        actividad.progreso > 0 ->
            "EN PROCESO"

        else ->
            "PENDIENTE"
    }
}

fun actividadesUrgentes(
    lista: List<ActividadFormativa>
): List<ActividadFormativa> {

    return lista.filter {

        it.progreso < 100 &&
                it.diasRestantes <= 2
    }
}

fun promedioProgreso(
    lista: List<ActividadFormativa>
): Double {

    if (lista.isEmpty())
        return 0.0

    return lista
        .map { it.progreso }
        .average()
}

fun buscarPorTitulo(
    lista: List<ActividadFormativa>,
    texto: String
): List<ActividadFormativa> {

    return lista.filter {

        it.titulo.trim()
            .contains(
                texto.trim(),
                ignoreCase = true
            )
    }
}