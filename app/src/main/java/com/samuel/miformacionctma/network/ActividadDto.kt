package com.samuel.miformacionctma.network

import com.google.gson.annotations.SerializedName

data class ActividadDto(
    @SerializedName("id") val id: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("fechaInicio") val fechaInicio: String,
    @SerializedName("fechaFin") val fechaFin: String,
    @SerializedName("progreso") val progreso: Int,
    @SerializedName("prioridad") val prioridad: String
)
