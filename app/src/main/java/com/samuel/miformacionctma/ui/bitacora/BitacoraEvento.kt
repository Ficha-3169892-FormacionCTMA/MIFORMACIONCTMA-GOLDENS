package com.samuel.miformacionctma.ui.bitacora

sealed class BitacoraEvento {
    data class TituloCambiado(val valor: String) : BitacoraEvento()
    data class ContenidoCambiado(val valor: String) : BitacoraEvento()
    data class HorasCambiadas(val valor: String) : BitacoraEvento()
    object GuardarBitacora : BitacoraEvento()
    object LimpiarMensaje : BitacoraEvento()
}
