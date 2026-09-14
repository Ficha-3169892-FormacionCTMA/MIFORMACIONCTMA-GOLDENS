package com.samuel.miformacionctma.network

import kotlinx.serialization.Serializable

@Serializable
data class PerfilDto(
    val id: String,
    val correo: String,
    val nombre: String?,
    val rol: String
)
