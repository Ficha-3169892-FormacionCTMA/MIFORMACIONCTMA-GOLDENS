package com.samuel.miformacionctma

import com.samuel.miformacionctma.network.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BitacoraApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `cuando aprendiz intenta ver notas de otro aprendiz retorna HTTP 403 Forbidden`() = runBlocking {
        // Simula respuesta 403 del backend
        val mockResponse = MockResponse()
            .setResponseCode(403)
            .setBody("""{"error": "Forbidden", "message": "Acceso denegado a datos de otro aprendiz"}""")
        mockWebServer.enqueue(mockResponse)

        val response = apiService.obtenerNotasAprendiz(aprendizId = "1002")

        assertEquals(403, response.code()) // Validar status code HTTP
        assertFalse(response.isSuccessful)
        assertTrue(response.errorBody()?.string()?.contains("Acceso denegado") == true)
    }

    @Test
    fun `cuando bitacora supera 2MB retorna HTTP 422 Unprocessable Content`() = runBlocking {
        // Simula rechazo por regla de negocio en tamaño de archivo
        val mockResponse = MockResponse()
            .setResponseCode(422)
            .setBody("""{"message": "El archivo supera el limite maximo de 2 MB"}""")
        mockWebServer.enqueue(mockResponse)

        val response = apiService.subirBitacora(fileData = "dummy_data_3mb")

        assertEquals(422, response.code())
        assertFalse(response.isSuccessful)
    }
}