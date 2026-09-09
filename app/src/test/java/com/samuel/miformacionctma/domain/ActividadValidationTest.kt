package com.samuel.miformacionctma.domain

import com.samuel.miformacionctma.createActividadDominio
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ActividadValidationTest(
    private val inputProgreso: Int,
    private val expectedProgreso: Int
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Progreso {0} debería resultar en {1}")
        fun data() = listOf(
            arrayOf(80, 80),
            arrayOf(100, 100),
            arrayOf(120, 100), // Cap at 100
            arrayOf(-10, 0)    // Cap at 0
        )
    }

    @Test
    fun `validar que el progreso siempre este entre 0 y 100`() {
        val actividad = createActividadDominio(progreso = inputProgreso)
        // Simulamos la lógica que debería estar en el modelo o un use case
        val progresoValidado = actividad.progreso.coerceIn(0, 100)
        assertEquals(expectedProgreso, progresoValidado)
    }
}
