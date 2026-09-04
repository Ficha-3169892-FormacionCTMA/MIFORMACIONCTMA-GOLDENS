package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.data.local.entities.ActividadEntity
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.AppViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(viewModel: AppViewModel, navController: NavController) {
    val actividades by viewModel.actividades.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividades") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == "INSTRUCTOR") {
                FloatingActionButton(onClick = { navController.navigate("formulario_actividad") }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Actividad")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar (HU-09)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar por nombre o competencia...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            if (showFilters) {
                FilterSection(viewModel)
            }

            if (actividades.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron actividades", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(actividades) { actividad ->
                        ActividadItem(actividad) {
                            navController.navigate("detalle/${actividad.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(viewModel: AppViewModel) {
    val filterPrioridad by viewModel.filterPrioridad.collectAsState()
    val filterEstado by viewModel.filterEstado.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Prioridad:", fontWeight = FontWeight.Bold)
        Row {
            Prioridad.entries.forEach { p ->
                FilterChip(
                    selected = filterPrioridad == p,
                    onClick = { viewModel.setFilterPrioridad(if (filterPrioridad == p) null else p) },
                    label = { Text(p.name) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
        Text("Estado:", fontWeight = FontWeight.Bold)
        Row {
            listOf("PENDIENTE", "EN_PROCESO", "COMPLETADA").forEach { e ->
                FilterChip(
                    selected = filterEstado == e,
                    onClick = { viewModel.setFilterEstado(if (filterEstado == e) null else e) },
                    label = { Text(e.replace("_", " ")) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
        Button(
            onClick = {
                viewModel.setFilterPrioridad(null)
                viewModel.setFilterEstado(null)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Limpiar Filtros")
        }
    }
}

@Composable
fun ActividadItem(actividad: ActividadEntity, onClick: () -> Unit) {
    val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), actividad.fechaFin)
    val colorPrioridad = when (actividad.prioridad) {
        Prioridad.ALTA -> Color.Red
        Prioridad.MEDIA -> Color(0xFFFFA500)
        Prioridad.BAJA -> Color(0xFF39A900)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = colorPrioridad,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(12.dp)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (actividad.progreso == 100) "Completada" else "${actividad.progreso}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (actividad.progreso == 100) Color(0xFF39A900) else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = actividad.descripcion ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Vence: ${actividad.fechaFin}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = when {
                        actividad.progreso == 100 -> "FINALIZADO"
                        diasRestantes < 0 -> "VENCIDO"
                        diasRestantes <= 2 -> "URGENTE ($diasRestantes días)"
                        else -> "$diasRestantes días restantes"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (diasRestantes < 0 || (diasRestantes <= 2 && actividad.progreso < 100)) Color.Red else Color.Unspecified
                )
            }
        }
    }
}
