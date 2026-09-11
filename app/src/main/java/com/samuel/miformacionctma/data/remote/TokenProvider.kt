package com.samuel.miformacionctma.data.remote

/**
 * Proveedor ficticio de tokens para simular autenticación en las peticiones.
 */
class TokenProvider {
    fun getToken(): String? = "MOCK_JWT_TOKEN_SEMANA_8"
}
