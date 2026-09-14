package com.samuel.miformacionctma.ui.screens

import android.net.Uri

/**
 * Estado que maneja la información del archivo multimedia antes de confirmarse.
 */
data class MediaSelectorState(
    val uri: Uri? = null,
    val nombre: String = "",
    val mimeType: String = "",
    val tamanoBytes: Long = 0L,
    val error: String? = null,
    val estadoSincronizacion: String = "LOCAL" // LOCAL, SUBIENDO, SINCRONIZADA, FALLIDA
)
