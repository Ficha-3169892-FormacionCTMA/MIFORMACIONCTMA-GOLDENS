package com.samuel.miformacionctma

import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.model.estadoActividad
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ActividadLogicTest {

    @Test
    fun `TC-01-2 Validar estado COMPLETADA cuando el progreso es 100`() {
        val actividad = ActividadFormativa(
            id = 1,
            titulo = "Test",
            descripcion = null,
            fechaInicio = LocalDate.now(),
            fechaFin = LocalDate.now().plusDays(5),
            progreso = 100,
            diasRestantes = 5,
            prioridad = Prioridad.MEDIA
        )
        
        assertEquals("COMPLETADA", estadoActividad(actividad))
    }

    @Test
    fun `TC-01-2 Validar estado VENCIDA cuando los dias restantes son negativos`() {
        val actividad = ActividadFormativa(
            id = 2,
            titulo = "Test Vencido",
            descripcion = null,
            fechaInicio = LocalDate.now().minusDays(10),
            fechaFin = LocalDate.now().minusDays(2),
            progreso = 50,
            diasRestantes = -2,
            prioridad = Prioridad.ALTA
        )
        
        assertEquals("VENCIDA", estadoActividad(actividad))
    }
}
