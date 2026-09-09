package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.ListadoUiState
import com.samuel.miformacionctma.ui.OperacionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(viewModel: AppViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilters by remember { mutableStateOf(false) }

    LaunchedEffect(operacionState) {
        when (operacionState) {
            is OperacionUiState.Fallida -> {
                snackbarHostState.showSnackbar((operacionState as OperacionUiState.Fallida).mensaje)
                viewModel.resetOperacionState()
            }
            OperacionUiState.Exitosa -> {
                snackbarHostState.showSnackbar("Operación realizada con éxito")
                viewModel.resetOperacionState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Actividades") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("formulario_actividad") }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Actividad")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Barra de búsqueda reactiva
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Buscar actividades...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            if (showFilters) {
                FilterSection(viewModel)
            }

            // Indicador de operación en curso
            if (operacionState is OperacionUiState.EnCurso) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Procesando...", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Manejo de estados de listado
            when (uiState) {
                is ListadoUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ListadoUiState.Contenido -> {
                    val lista = (uiState as ListadoUiState.Contenido).data
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista, key = { it.id }) { actividad ->
                            ActividadItem(actividad, onClick = { navController.navigate("detalle/${actividad.id}") })
                        }
                    }
                }
                is ListadoUiState.Vacio -> {
                    EmptyState(onAction = { viewModel.setSearchQuery("") })
                }
                is ListadoUiState.Error -> {
                    ErrorState(
                        mensaje = (uiState as ListadoUiState.Error).mensaje,
                        onReintentar = { viewModel.setSearchQuery(searchQuery) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(viewModel: AppViewModel) {
    val priorities = listOf("TODAS", "ALTA", "MEDIA", "BAJA")
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Filtrar por prioridad:", style = MaterialTheme.typography.labelLarge)
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            priorities.forEach { p ->
                FilterChip(
                    selected = false, // Conectar con State en VM si es necesario
                    onClick = { viewModel.setFilterPrioridad(p) },
                    label = { Text(p) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ActividadItem(actividad: ActividadFormativa, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(actividad.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(actividad.descripcion ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            if (actividad.progreso == 100) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Completada", tint = Color(0xFF39A900))
            } else {
                Text("${actividad.progreso}%", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun EmptyState(onAction: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Text("No se encontraron actividades", color = Color.Gray)
        TextButton(onClick = onAction) { Text("Limpiar búsqueda") }
    }
}

@Composable
fun ErrorState(mensaje: String, onReintentar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Text(mensaje, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Button(onClick = onReintentar, modifier = Modifier.padding(top = 16.dp)) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}
