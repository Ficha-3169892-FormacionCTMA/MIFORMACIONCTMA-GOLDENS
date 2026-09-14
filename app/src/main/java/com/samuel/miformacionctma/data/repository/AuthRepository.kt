package com.samuel.miformacionctma.data.repository

import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.network.PerfilDto
import com.samuel.miformacionctma.network.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

open class AuthRepository(
    private val preferencesRepository: UserPreferencesRepository
) {
    private val authClient = SupabaseProvider.client.auth
    private val postgrestClient = SupabaseProvider.client.postgrest

    val sessionStatus: StateFlow<SessionStatus> = authClient.sessionStatus
    val currentRole: Flow<String?> = preferencesRepository.userRole
    
    val isSessionActive: Boolean
        get() = authClient.currentSessionOrNull() != null

    suspend fun signUp(email: String, password: String, rol: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            authClient.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("rol", rol)
                }
            }
            
            val user = authClient.currentUserOrNull()
            if (user != null) {
                preferencesRepository.saveUser(
                    id = user.id,
                    role = rol,
                    token = authClient.currentAccessTokenOrNull() ?: "",
                    name = email.substringBefore("@")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            authClient.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = authClient.currentUserOrNull() ?: throw Exception("Error al obtener usuario autenticado")
            
            // Consultar el perfil correspondiente en la tabla perfiles
            val response = postgrestClient["perfiles"].select {
                filter {
                    eq("id", user.id)
                }
            }
            val perfil = response.decodeSingle<PerfilDto>()

            preferencesRepository.saveUser(
                id = user.id,
                role = perfil.rol,
                token = authClient.currentAccessTokenOrNull() ?: "",
                name = perfil.nombre ?: email.substringBefore("@")
            )
            
            Result.success(perfil.rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            authClient.signOut()
            preferencesRepository.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
