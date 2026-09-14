package com.samuel.miformacionctma.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {
    // Valores por defecto configurables en producción/entorno
    private const val SUPABASE_URL = "https://mksezhbttckzznbcjfvd.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_V9JVjLH6cr8s9XKbNiAqFw_9b8eBfej"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
            install(Realtime)
        }
    }
}
