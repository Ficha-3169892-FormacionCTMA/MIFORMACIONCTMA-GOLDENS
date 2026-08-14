package com.samuel.miformacionctma

import com.samuel.miformacionctma.model.*
import org.junit.Assert.*
import org.junit.Test

class ActividadPruebasTest {

    @Test
    fun `CP-08 - Entrega exitosa con URL valida actualiza progreso a 100 y estado ENTREGADA`() {
        // Precondición
        val actividad = ActividadFormativa(
            id = 2,
            titulo = "Variables y tipos",
            progreso = 90,
            diasRestantes = 1,
            prioridad = Prioridad.ALTA,
            estado = EstadoActividadEnum.EN_PROCESO
        )
        val urlValida = "https://github.com/paboncito666/GuiaAndroid1.git"

        // Ejecución
        val resultado = entregarActividad(actividad, urlValida)

        // Verificación (PASS)
        assertTrue(resultado.isSuccess)
        val actividadResultado = resultado.getOrNull()
        assertNotNull(actividadResultado)
        assertEquals(100, actividadResultado?.progreso)
        assertEquals(EstadoActividadEnum.ENTREGADA, actividadResultado?.estado)
        assertEquals(urlValida, actividadResultado?.urlEvidencia)
    }

    @Test
    fun `CP-09 - Entrega fallida con URL vacia genera excepcion y no cambia estado`() {
        // Precondición
        val actividad = ActividadFormativa(
            id = 2,
            titulo = "Variables y tipos",
            progreso = 90,
            diasRestantes = 1,
            prioridad = Prioridad.ALTA,
            estado = EstadoActividadEnum.EN_PROCESO
        )
        val urlInvalida = "   " // Espacios en blanco

        // Ejecución
        val resultado = entregarActividad(actividad, urlInvalida)

        // Verificación (PASS)
        assertTrue(resultado.isFailure)
        assertEquals("La URL de evidencia no puede estar vacía", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `CP-02 - Límite exacto de progreso 0 retorna estado Pendiente`() {
        val actividad = ActividadFormativa(
            id = 10,
            titulo = "MVVM",
            progreso = 0,
            diasRestantes = 10,
            prioridad = Prioridad.ALTA
        )
        val estadoTexto = estadoActividad(actividad)
        assertEquals("Pendiente", estadoTexto)
    }
}