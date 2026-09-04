package com.samuel.miformacionctma

import com.samuel.miformacionctma.ui.ActividadViewModel
import com.samuel.miformacionctma.ui.formulario.FormularioActividadEvento
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FormularioActividadTest {

    private lateinit var viewModel: ActividadViewModel

    @Before
    fun setup() {
        viewModel = ActividadViewModel()
    }

    @Test
    fun `TC-02-1 Validar error cuando fecha fin es anterior a inicio`() {
        val hoy = LocalDate.now().toString()
        val ayer = LocalDate.now().minusDays(1).toString()
        
        viewModel.onEvento(FormularioActividadEvento.FechaInicioCambiada(hoy))
        viewModel.onEvento(FormularioActividadEvento.FechaFinCambiada(ayer))
        
        assertNotNull("Debería existir un error de fechas", viewModel.uiState.fechasError)
        assertFalse("El formulario no debe ser válido", viewModel.uiState.esValido)
    }

    @Test
    fun `TC-02-2 El boton guardar debe estar deshabilitado con titulo corto`() {
        viewModel.onEvento(FormularioActividadEvento.TituloCambiado("AB")) // Solo 2 letras
        
        assertNotNull("Debería existir un error de título", viewModel.uiState.tituloError)
        assertFalse(viewModel.uiState.esValido)
    }
}
