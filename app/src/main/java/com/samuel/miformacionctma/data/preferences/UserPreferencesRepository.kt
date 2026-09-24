package com.samuel.miformacionctma.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SIZE_SCALE = stringPreferencesKey("font_size_scale")
        val FILTRO_PRIORIDAD = stringPreferencesKey("filtro_prioridad")
        val NOTIFICACIONES_ENABLED = booleanPreferencesKey("notificaciones_enabled")
    }

    val userId: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_ID] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_ROLE] }
    val userName: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_EMAIL] }
    val themeMode: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.THEME_MODE] ?: "SYSTEM" }
    val fontSizeScale: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.FONT_SIZE_SCALE] ?: "MEDIUM" }
    val filtroPrioridad: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.FILTRO_PRIORIDAD] ?: "TODAS" }
    val notificacionesEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.NOTIFICACIONES_ENABLED] ?: false }

    suspend fun saveUser(id: String, role: String, token: String, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_ID] = id
            prefs[PreferencesKeys.USER_ROLE] = role
            prefs[PreferencesKeys.AUTH_TOKEN] = token
            prefs[PreferencesKeys.USER_NAME] = name
            prefs[PreferencesKeys.USER_EMAIL] = email
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferencesKeys.USER_ID)
            prefs.remove(PreferencesKeys.USER_ROLE)
            prefs.remove(PreferencesKeys.AUTH_TOKEN)
            prefs.remove(PreferencesKeys.USER_NAME)
            prefs.remove(PreferencesKeys.USER_EMAIL)
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[PreferencesKeys.THEME_MODE] = mode }
    }

    suspend fun setFiltroPrioridad(prioridad: String) {
        context.dataStore.edit { prefs -> prefs[PreferencesKeys.FILTRO_PRIORIDAD] = prioridad }
    }

    suspend fun setFontSizeScale(scale: String) {
        context.dataStore.edit { prefs -> prefs[PreferencesKeys.FONT_SIZE_SCALE] = scale }
    }

    suspend fun setNotificacionesEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[PreferencesKeys.NOTIFICACIONES_ENABLED] = enabled }
    }
}
