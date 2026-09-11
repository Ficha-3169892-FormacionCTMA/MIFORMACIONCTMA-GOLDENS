package com.samuel.miformacionctma

import app.cash.turbine.test
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.fakes.FakeAppRepository
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.ListadoUiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAppRepository
    private lateinit var mockPrefs: UserPreferencesRepository
    private lateinit var viewModel: AppViewModel
    
    private val filtroPrioridadFlow = MutableStateFlow("TODAS")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAppRepository()
        mockPrefs = mockk(relaxed = true)
        
        // Setup mock to return a flow that emits values
        every { mockPrefs.filtroPrioridad } returns filtroPrioridadFlow
        
        viewModel = AppViewModel(mockk(relaxed = true), fakeRepository, mockPrefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `CA-01 abrir sin datos pasa por Cargando y termina en Vacio`() = runTest {
        viewModel.uiState.test {
            // Initial state from stateIn
            assertEquals(ListadoUiState.Cargando, awaitItem())
            
            // Advance time to pass debounce(300)
            advanceTimeBy(301)
            
            // Fake repository is empty by default, so it should emit Vacio
            assertEquals(ListadoUiState.Vacio, awaitItem())
        }
    }

    @Test
    fun `CA-02 insertar una actividad actualiza Contenido automaticamente`() = runTest {
        viewModel.uiState.test {
            assertEquals(ListadoUiState.Cargando, awaitItem())
            
            // Initial empty state
            advanceTimeBy(301)
            assertEquals(ListadoUiState.Vacio, awaitItem())

            // Insert data
            val nuevaActividad = createActividadDominio(id = 1L, titulo = "Nueva Tarea")
            fakeRepository.emitActividades(listOf(nuevaActividad))

            // The flow should react
            val state = awaitItem()
            assertTrue(state is ListadoUiState.Contenido)
            assertEquals(1, (state as ListadoUiState.Contenido).data.size)
            assertEquals("Nueva Tarea", state.data[0].titulo)
        }
    }

    @Test
    fun `CA-05 fallo del repository en refresh se refleja en operacionState`() = runTest {
        fakeRepository.shouldReturnError = true
        
        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.operacionState.value
        assertTrue(state is com.samuel.miformacionctma.ui.OperacionUiState.Fallida)
    }
}
