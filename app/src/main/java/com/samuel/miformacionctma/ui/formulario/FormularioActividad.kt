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
        Text(
            text = "Nueva Actividad",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.titulo,
            onValueChange = { onEvento(FormularioActividadEvento.TituloCambiado(it)) },
            label = { Text("Título (3-80 chars)") },
            isError = uiState.tituloError != null,
            supportingText = { uiState.tituloError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = { onEvento(FormularioActividadEvento.DescripcionCambiada(it)) },
            label = { Text("Descripción (opcional, max 240)") },
            isError = uiState.descripcionError != null,
            supportingText = { uiState.descripcionError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        OutlinedTextField(
            value = uiState.fechaInicio,
            onValueChange = { onEvento(FormularioActividadEvento.FechaInicioCambiada(it)) },
            label = { Text("Fecha Inicio (yyyy-mm-dd)") },
            isError = uiState.fechasError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.fechaFin,
            onValueChange = { onEvento(FormularioActividadEvento.FechaFinCambiada(it)) },
            label = { Text("Fecha Fin (yyyy-mm-dd)") },
            isError = uiState.fechasError != null,
            supportingText = { uiState.fechasError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.progreso,
            onValueChange = { input ->
                val filtrado = input.filter { it.isDigit() }
                onEvento(FormularioActividadEvento.ProgresoCambiado(filtrado))
            },
            label = { Text("Progreso (0-100%)") },
            isError = uiState.progresoError != null,
            supportingText = { uiState.progresoError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}