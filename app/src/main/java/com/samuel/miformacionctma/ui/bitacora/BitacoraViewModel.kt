package com.samuel.miformacionctma.ui.bitacora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.miformacionctma.model.Bitacora
import com.samuel.miformacionctma.model.ValidacionesNegocio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class BitacoraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BitacoraUiState())
    val uiState: StateFlow<BitacoraUiState> = _uiState.asStateFlow()

    fun onEvento(evento: BitacoraEvento) {
        when (evento) {
            is BitacoraEvento.TituloCambiado -> {
                _uiState.update { it.copy(titulo = evento.valor, mensajeExito = null) }
            }
            is BitacoraEvento.ContenidoCambiado -> {
                _uiState.update { it.copy(contenido = evento.valor, mensajeExito = null) }
            }
            is BitacoraEvento.HorasCambiadas -> {
                _uiState.update { it.copy(horas = evento.valor, mensajeExito = null) }
            }
            BitacoraEvento.GuardarBitacora -> guardarBitacora()
            BitacoraEvento.LimpiarMensaje -> _uiState.update { it.copy(mensajeExito = null) }
        }
    }

    private fun guardarBitacora() {
        val current = _uiState.value
        val horasInt = current.horas.toIntOrNull() ?: 0
        
        val errores = ValidacionesNegocio.validarBitacora(
            current.titulo,
            current.contenido,
            horasInt
        )

        if (errores.isEmpty()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errores = emptyList()) }
                
                // Simulación de red/db
                delay(1500)
                
                val nuevaBitacora = Bitacora(
                    id = System.currentTimeMillis(),
                    fecha = LocalDate.now(),
                    titulo = current.titulo,
                    contenido = current.contenido,
                    horas = horasInt
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        mensajeExito = "Bitácora guardada con éxito",
                        listaBitacoras = it.listaBitacoras + nuevaBitacora,
                        titulo = "",
                        contenido = "",
                        horas = ""
                    )
                }
            }
        } else {
            _uiState.update { it.copy(errores = errores) }
        }
    }
}
