package com.samuel.miformacionctma

import com.samuel.miformacionctma.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ActividadPruebasTest {

    private val fechaHoy = LocalDate.now()

    // CP-01
    @Test
    fun `CP-01 - Progreso negativo lanza IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            ActividadFormativa(
                id = 1L,
                titulo = "Kotlin",
                descripcion = null,
                fechaInicio = fechaHoy,
                fechaFin = fechaHoy.plusDays(5),
                progreso = -10,
                diasRestantes = 5,
                prioridad = Prioridad.BAJA
            )
        }
    }

    // CP-02
    @Test
    fun `CP-02 - Limite exacto de progreso 0 retorna estado Pendiente`() {
        val actividad = ActividadFormativa(
            id = 10L,
            titulo = "MVVM",
            descripcion = null,
            fechaInicio = fechaHoy,
            fechaFin = fechaHoy.plusDays(10),
            progreso = 0,
            diasRestantes = 10,
            prioridad = Prioridad.ALTA
        )

        val estadoTexto = estadoActividad(actividad)

        assertEquals("PENDIENTE", estadoTexto)
    }

    // CP-03
    @Test
    fun `CP-03 - Progreso mayor que 0 retorna estado En Proceso`() {
        val actividad = ActividadFormativa(
            id = 3L,
            titulo = "Layouts",
            descripcion = null,
            fechaInicio = fechaHoy,
            fechaFin = fechaHoy.plusDays(5),
            progreso = 50,
            diasRestantes = 5,
            prioridad = Prioridad.MEDIA
        )

        assertEquals("EN PROCESO", estadoActividad(actividad))
    }

    // CP-04
    @Test
    fun `CP-04 - Progreso 99 retorna estado En Proceso`() {
        val actividad = ActividadFormativa(
            id = 4L,
            titulo = "Navegacion",
            descripcion = null,
            fechaInicio = fechaHoy,
            fechaFin = fechaHoy.plusDays(1),
            progreso = 99,
            diasRestantes = 1,
            prioridad = Prioridad.ALTA
        )

        assertEquals("EN PROCESO", estadoActividad(actividad))
    }

    // CP-05
    @Test
    fun `CP-05 - Progreso 100 retorna estado Completada`() {
        val actividad = ActividadFormativa(
            id = 5L,
            titulo = "Proyecto Android",
            descripcion = null,
            fechaInicio = fechaHoy,
            fechaFin = fechaHoy,
            progreso = 100,
            diasRestantes = 0,
            prioridad = Prioridad.ALTA
        )

        assertEquals("COMPLETADA", estadoActividad(actividad))
    }
}
