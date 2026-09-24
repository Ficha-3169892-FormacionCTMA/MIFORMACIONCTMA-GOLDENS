package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.createActividadSupabaseDto
import com.samuel.miformacionctma.data.local.AppDatabase
import com.samuel.miformacionctma.data.local.dao.ActividadDao
import com.samuel.miformacionctma.network.NetworkError
import com.samuel.miformacionctma.network.NetworkResult
import com.samuel.miformacionctma.network.RemoteActividadDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppRepositoryTest {

    private lateinit var repository: AppRepository
    private val mockDb = mockk<AppDatabase>()
    private val mockDao = mockk<ActividadDao>(relaxed = true)
    private val mockRemoteDataSource = mockk<RemoteActividadDataSource>()

    @Before
    fun setup() {
        every { mockDb.actividadDao() } returns mockDao
        repository = AppRepository(mockDb, mockRemoteDataSource)
    }

    @After
    fun teardown() {
        // Limpieza de mocks
        confirmVerified(mockDao, mockRemoteDataSource)
    }

    @Test
    fun `refreshActividades llama al remote y guarda en local al tener exito`() = runTest {
        // GIVEN
        val fakeDtos = listOf(createActividadSupabaseDto(id = 100L, titulo = "Sincronizada"))
        coEvery { mockRemoteDataSource.getActividades() } returns NetworkResult.Success(fakeDtos)

        // WHEN
        val result = repository.refreshActividades()

        // THEN
        assertTrue(result is NetworkResult.Success)
        coVerify(exactly = 1) { mockRemoteDataSource.getActividades() }
        coVerify(exactly = 1) { mockDao.getActividadByRemoteId(100L) }
        coVerify(exactly = 1) { mockDao.insertActividad(any()) }
    }

    @Test
    fun `refreshActividades devuelve error de red y NO toca el DAO local`() = runTest {
        // GIVEN
        coEvery { mockRemoteDataSource.getActividades() } returns NetworkResult.Error(NetworkError.SinConexion)

        // WHEN
        val result = repository.refreshActividades()

        // THEN
        assertTrue(result is NetworkResult.Error)
        assertEquals(NetworkError.SinConexion, (result as NetworkResult.Error).errorType)
        
        coVerify(exactly = 1) { mockRemoteDataSource.getActividades() }
        coVerify(exactly = 0) { mockDao.getActividadByRemoteId(any()) }
        coVerify(exactly = 0) { mockDao.insertActividad(any()) }
    }
}
