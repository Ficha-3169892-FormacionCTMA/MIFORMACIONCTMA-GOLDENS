package com.samuel.miformacionctma.network

import io.github.jan.supabase.postgrest.postgrest

class RemoteActividadDataSource {

    suspend fun getActividades(): NetworkResult<List<ActividadSupabaseDto>> {
        return try {
            val list = SupabaseProvider.client.postgrest["actividad_formativa"]
                .select()
                .decodeList<ActividadSupabaseDto>()
            NetworkResult.Success(list)
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError.SinConexion, e.message ?: "Error al descargar actividades")
        }
    }
}
