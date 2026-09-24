package com.samuel.miformacionctma

import com.samuel.miformacionctma.network.BitacoraSupabaseDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BitacoraApiTest {

    @Test
    fun `BitacoraSupabaseDto serializa y deserializa correctamente los campos`() {
        val dtoOriginal = BitacoraSupabaseDto(
            id = 10L,
            autorId = "usr_123",
            fecha = "2026-10-15",
            titulo = "Bitácora diaria",
            contenido = "Trabajé en el módulo de bitácoras",
            horas = 4
        )

        val jsonText = Json.encodeToString(BitacoraSupabaseDto.serializer(), dtoOriginal)
        val dtoDeserializado = Json.decodeFromString(BitacoraSupabaseDto.serializer(), jsonText)

        assertEquals(dtoOriginal.id, dtoDeserializado.id)
        assertEquals(dtoOriginal.autorId, dtoDeserializado.autorId)
        assertEquals(dtoOriginal.fecha, dtoDeserializado.fecha)
        assertEquals(dtoOriginal.titulo, dtoDeserializado.titulo)
        assertEquals(dtoOriginal.contenido, dtoDeserializado.contenido)
        assertEquals(dtoOriginal.horas, dtoDeserializado.horas)
    }

    @Test
    fun `BitacoraSupabaseDto maneja id nulo por defecto para nuevos inserts`() {
        val dto = BitacoraSupabaseDto(
            autorId = "usr_456",
            fecha = "2026-10-15",
            titulo = "Nueva bitácora",
            contenido = "Prueba sin id remoto",
            horas = 2
        )

        assertNull(dto.id)
    }
}
