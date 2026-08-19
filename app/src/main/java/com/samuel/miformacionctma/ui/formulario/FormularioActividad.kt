package com.samuel.miformacionctma.ui.formulario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FormularioActividad(
    uiState: FormularioActividadUiState,
    guardando: Boolean,
    onEvento: (FormularioActividadEvento) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Campo Título
        OutlinedTextField(
            value = uiState.titulo,
            onValueChange = { onEvento(FormularioActividadEvento.TituloCambiado(it)) },
            label = { Text("Título de la Actividad") },
            isError = uiState.tituloError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        uiState.tituloError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Campo Descripción
        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = { onEvento(FormularioActividadEvento.DescripcionCambiada(it)) },
            label = { Text("Descripción (opcional)") },
            isError = uiState.descripcionError != null,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )
        uiState.descripcionError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Campo Fecha Inicio
        OutlinedTextField(
            value = uiState.fechaInicio,
            onValueChange = { onEvento(FormularioActividadEvento.FechaInicioCambiada(it)) },
            label = { Text("Fecha Inicio (YYYY-MM-DD)") },
            isError = uiState.fechasError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Campo Fecha Fin
        OutlinedTextField(
            value = uiState.fechaFin,
            onValueChange = { onEvento(FormularioActividadEvento.FechaFinCambiada(it)) },
            label = { Text("Fecha Fin (YYYY-MM-DD)") },
            isError = uiState.fechasError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        uiState.fechasError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Campo Progreso
        OutlinedTextField(
            value = uiState.progreso,
            onValueChange = { input ->
                // Filtramos caracteres no numéricos en tiempo de edición
                val filtrado = input.filter { it.isDigit() }
                onEvento(FormularioActividadEvento.ProgresoCambiado(filtrado))
            },
            label = { Text("Progreso (%)") },
            isError = uiState.progresoError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        uiState.progresoError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Acciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            TextButton(
                enabled = !guardando,
                onClick = { onEvento(FormularioActividadEvento.Cancelar) }
            ) {
                Text("Cancelar")
            }

            Button(
                enabled = uiState.esValido && !guardando,
                onClick = { onEvento(FormularioActividadEvento.Guardar) }
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}