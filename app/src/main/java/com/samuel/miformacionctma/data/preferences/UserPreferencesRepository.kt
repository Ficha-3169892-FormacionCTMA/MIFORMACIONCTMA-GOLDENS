package com.samuel.miformacionctma.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_NAME = stringPreferencesKey("user_name")
        val THEME_MODE = stringPreferencesKey("theme_mode") // "LIGHT", "DARK", "SYSTEM"
        val FONT_SIZE_SCALE = stringPreferencesKey("font_size_scale") // "SMALL", "MEDIUM", "LARGE"
    }

    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[USER_ROLE] }
    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME] }
    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "SYSTEM" }
    val fontSizeScale: Flow<String> = context.dataStore.data.map { it[FONT_SIZE_SCALE] ?: "MEDIUM" }

    suspend fun saveUser(id: String, role: String, token: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_ROLE] = role
            prefs[AUTH_TOKEN] = token
            prefs[USER_NAME] = name
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_ROLE)
            prefs.remove(AUTH_TOKEN)
            prefs.remove(USER_NAME)
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setFontSizeScale(scale: String) {
        context.dataStore.edit { it[FONT_SIZE_SCALE] = scale }
    }
}
