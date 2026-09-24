package com.samuel.miformacionctma.data.repository

import android.util.Log
import com.samuel.miformacionctma.data.preferences.UserPreferencesRepository
import com.samuel.miformacionctma.network.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializable
data class PerfilDto(
    val id: String,
    val correo: String? = null,
    val nombre: String? = null,
    val rol: String? = null
)

class AuthRepository(
    private val preferencesRepository: UserPreferencesRepository
) {
    val isSessionActive: Boolean
        get() = SupabaseProvider.client.auth.currentUserOrNull() != null

    suspend fun signUp(email: String, password: String, rol: String): Result<Unit> {
        return try {
            SupabaseProvider.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("rol", rol)
                }
            }
            val user = SupabaseProvider.client.auth.currentUserOrNull()
            if (user != null) {
                val userId = user.id
                val userRole = user.userMetadata?.get("rol")?.jsonPrimitive?.content ?: rol
                val name = user.userMetadata?.get("nombre")?.jsonPrimitive?.content ?: email.substringBefore("@")
                val token = SupabaseProvider.client.auth.currentAccessTokenOrNull() ?: ""
                preferencesRepository.saveUser(
                    id = userId,
                    role = userRole,
                    token = token,
                    name = name,
                    email = email
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signUp: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            SupabaseProvider.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = SupabaseProvider.client.auth.currentUserOrNull()
                ?: throw Exception("No se pudo obtener la sesión tras iniciar sesión en Supabase.")
            val userId = user.id
            val token = SupabaseProvider.client.auth.currentAccessTokenOrNull() ?: ""

            var rolEncontrado: String? = null
            var nombreEncontrado: String? = null

            try {
                val perfil = SupabaseProvider.client.postgrest["perfiles"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<PerfilDto>()
                rolEncontrado = perfil?.rol
                nombreEncontrado = perfil?.nombre
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al consultar tabla 'perfiles' en Postgrest: ${e.message}", e)
            }

            val finalRol = rolEncontrado
                ?: user.userMetadata?.get("rol")?.jsonPrimitive?.content
                ?: "aprendiz"
            val finalName = nombreEncontrado
                ?: user.userMetadata?.get("nombre")?.jsonPrimitive?.content
                ?: email.substringBefore("@")

            preferencesRepository.saveUser(
                id = userId,
                role = finalRol,
                token = token,
                name = finalName,
                email = email
            )

            Result.success(finalRol)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signIn: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            SupabaseProvider.client.auth.signOut()
            preferencesRepository.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signOut: ${e.message}", e)
            Result.failure(e)
        }
    }
}
