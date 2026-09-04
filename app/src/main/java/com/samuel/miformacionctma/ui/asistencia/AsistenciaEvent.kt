package com.samuel.miformacionctma.ui.asistencia

sealed class AsistenciaEvent {
    data class RegistrarAsistencia(val presente: Boolean, val observacion: String?) : AsistenciaEvent()
    object ErrorConsumido : AsistenciaEvent()
    object MensajeExitoConsumido : AsistenciaEvent()
    object CargarAsistencias : AsistenciaEvent()
}
