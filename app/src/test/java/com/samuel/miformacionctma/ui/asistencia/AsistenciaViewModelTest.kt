package com.samuel.miformacionctma.ui.asistencia

import app.cash.turbine.test
import com.samuel.miformacionctma.data.local.entities.AsistenciaEntity
import com.samuel.miformacionctma.data.repository.AppRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AsistenciaViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: AppRepository = mockk()
    private val userId = "user123"
    private lateinit var viewModel: AsistenciaViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock por defecto para evitar que el init falle
        every { repository.getAsistencias(any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando se inicia el ViewModel, carga las asistencias correctamente`() = runTest {
        // GIVEN
        val mockAsistencias = listOf(
            AsistenciaEntity(1, userId, LocalDate.now(), true, null)
        )
        every { repository.getAsistencias(userId) } returns flowOf(mockAsistencias)

        // WHEN
        viewModel = AsistenciaViewModel(repository, userId)
        
        // THEN
        viewModel.uiState.test {
            // StateFlow emite su valor actual inmediatamente. 
            // Con UnconfinedTestDispatcher, el flujo de 'init' se ejecuta rápido.
            val finalState = awaitItem()
            assertEquals("La lista de asistencias debe coincidir", mockAsistencias, finalState.asistencias)
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `cuando se registra asistencia con exito, actualiza el estado con mensaje de confirmacion`() = runTest {
        // GIVEN
        coEvery { repository.registrarAsistencia(userId, any(), any()) } coAnswers {
            delay(1) // Pequeño delay para que Turbine pueda capturar el estado intermedio isRegistrando = true
        }
        viewModel = AsistenciaViewModel(repository, userId)
        
        viewModel.uiState.test {
            awaitItem() // Ignorar estado inicial tras init
            
            // WHEN
            viewModel.onEvent(AsistenciaEvent.RegistrarAsistencia(true, "Nota"))
            
            // THEN
            // 1. Estado de carga
            assertTrue("Debería estar registrando", awaitItem().isRegistrando)
            
            // 2. Estado de éxito
            val successState = awaitItem()
            assertEquals("Asistencia registrada correctamente", successState.mensajeExito)
            assertFalse(successState.isRegistrando)
        }
        
        coVerify { repository.registrarAsistencia(userId, true, "Nota") }
    }

    @Test
    fun `cuando falla el registro de asistencia, muestra el mensaje de error correspondiente`() = runTest {
        // GIVEN
        coEvery { repository.registrarAsistencia(any(), any(), any()) } coAnswers {
            delay(1)
            throw Exception("Error de red")
        }
        viewModel = AsistenciaViewModel(repository, userId)
        
        viewModel.uiState.test {
            awaitItem() // Inicial
            
            // WHEN
            viewModel.onEvent(AsistenciaEvent.RegistrarAsistencia(false, null))

            // THEN
            assertTrue(awaitItem().isRegistrando) // Estado cargando
            
            val errorState = awaitItem()
            assertTrue("El error debe contener el mensaje", errorState.error?.contains("Error de red") == true)
            assertFalse(errorState.isRegistrando)
        }
    }
}
