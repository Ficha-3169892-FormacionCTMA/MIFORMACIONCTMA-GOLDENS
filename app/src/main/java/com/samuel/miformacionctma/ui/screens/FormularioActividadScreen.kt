package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.AppViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioActividadScreen(viewModel: AppViewModel, navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf(LocalDate.now()) }
    var fechaFin by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    var prioridad by remember { mutableStateOf(Prioridad.MEDIA) }

    var errorFechas by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Actividad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la actividad") },
                modifier = Modifier.fillMaxWidth(),
                isError = titulo.isEmpty()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Prioridad:")
            Row {
                Prioridad.values().forEach { p ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = prioridad == p, onClick = { prioridad = p })
                        Text(p.name)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Fecha Inicio: $fechaInicio")
            Text("Fecha Fin: $fechaFin")
            
            if (errorFechas) {
                Text("La fecha final debe ser posterior a la inicial", color = Color.Red)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (fechaFin.isBefore(fechaInicio)) {
                        errorFechas = true
                    } else if (titulo.isNotBlank()) {
                        viewModel.addActividad(titulo, descripcion, fechaInicio, fechaFin, prioridad)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A900))
            ) {
                Text("PUBLICAR ACTIVIDAD")
            }
        }
    }
}
