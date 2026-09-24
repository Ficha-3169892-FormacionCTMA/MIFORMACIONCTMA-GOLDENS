package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.network.NetworkError
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.ListadoUiState
import com.samuel.miformacionctma.ui.OperacionUiState
import com.samuel.miformacionctma.util.EstadoActividad
import com.samuel.miformacionctma.util.UserRoles
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(viewModel: AppViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val currentUserId by viewModel.userId.collectAsStateWithLifecycle()
    val actividadesConEstado by viewModel.actividadesConEstado.collectAsStateWithLifecycle()
    
    val mapaEstados = remember(actividadesConEstado) {
        actividadesConEstado.associate { it.first.id to it.second }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showFilters by remember { mutableStateOf(false) }
    var actividadABorrar by remember { mutableStateOf<ActividadFormativa?>(null) }

    // Sincronización al iniciar
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    // Manejo de mensajes de error de red
    LaunchedEffect(operacionState) {
        if (operacionState is OperacionUiState.Fallida) {
            val error = operacionState as OperacionUiState.Fallida
            val mensaje = error.mensaje ?: when (error.error) {
                NetworkError.NoAutorizado -> "Sesión expirada. Por favor, inicia sesión de nuevo."
                NetworkError.SinConexion -> "Sin conexión. Mostrando datos locales."
                else -> "Error de sincronización"
            }
            snackbarHostState.showSnackbar(mensaje)
        }
    }

    if (actividadABorrar != null) {
        AlertDialog(
            onDismissRequest = { actividadABorrar = null },
            title = { Text("¿Borrar esta actividad?") },
            text = { Text("Esta acción eliminará la actividad de forma permanente tanto localmente como en Supabase.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        actividadABorrar?.let { viewModel.deleteActividad(it) }
                        actividadABorrar = null
                    }
                ) {
                    Text("BORRAR", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { actividadABorrar = null }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Actividades")
                        lastUpdate?.let {
                            Text(
                                "Sincronizado: ${it.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
                    }
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                    }
                }
            )
        },
        floatingActionButton = {
            if (UserRoles.isInstructor(userRole)) {
                FloatingActionButton(onClick = { navController.navigate("formulario_actividad") }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Actividad")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Indicador de carga de red
            if (operacionState is OperacionUiState.EnCurso) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

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

            // Banner de reintento si falló la sincronización pero hay datos locales
            if (operacionState is OperacionUiState.Fallida && (operacionState as OperacionUiState.Fallida).error != NetworkError.NoAutorizado) {
                RetryBanner(onRetry = { viewModel.refresh() })
            }

            when (uiState) {
                is ListadoUiState.Cargando -> LoadingState()
                is ListadoUiState.Contenido -> {
                    val lista = (uiState as ListadoUiState.Contenido).data
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista, key = { it.id }) { actividad ->
                            ActividadItem(
                                actividad = actividad,
                                estado = mapaEstados[actividad.id],
                                userRole = userRole,
                                currentUserId = currentUserId,
                                onClick = { navController.navigate("detalle/${actividad.id}") },
                                onDelete = { actividadABorrar = actividad },
                                onReviewClick = if (actividad.remoteId != null) {
                                    { navController.navigate("revision/${actividad.remoteId}") }
                                } else null
                            )
                        }
                    }
                }
                is ListadoUiState.Vacio -> EmptyState(onAction = { viewModel.setSearchQuery("") })
                is ListadoUiState.Error -> ErrorState(
                    mensaje = (uiState as ListadoUiState.Error).mensaje,
                    onReintentar = { viewModel.refresh() }
                )
            }
        }
    }
}

@Composable
fun FilterSection(viewModel: AppViewModel) {
    val priorities = listOf("TODAS", "ALTA", "MEDIA", "BAJA")
    val currentFiltro by viewModel.filtroPrioridad.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Prioridad:", style = MaterialTheme.typography.labelLarge)
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            priorities.forEach { p ->
                FilterChip(
                    selected = currentFiltro == p,
                    onClick = { viewModel.setFilterPrioridad(p) },
                    label = { Text(p) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}

@Composable
fun RetryBanner(onRetry: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text(
                "No se pudo sincronizar",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) {
                Text("REINTENTAR")
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ActividadItem(
    actividad: ActividadFormativa,
    estado: EstadoActividad? = null,
    userRole: String? = null,
    currentUserId: String? = null,
    onDelete: (() -> Unit)? = null,
    onReviewClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (UserRoles.isAprendiz(userRole) && estado != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (badgeColor, label) = when (estado) {
                            EstadoActividad.COMPLETADA -> Color(0xFF2E7D32) to "Aprobada"
                            EstadoActividad.FALLIDA -> Color.Red to "Rechazada"
                            EstadoActividad.EN_REVISION -> Color(0xFFF9A825) to "En revisión"
                            EstadoActividad.PENDIENTE -> Color.Gray to "Pendiente"
                            EstadoActividad.VENCIDA -> Color(0xFFE65100) to "Vencida"
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(badgeColor, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = label, style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = actividad.descripcion ?: "Sin descripción",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(actividad.prioridad.name, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    text = "Hasta: ${actividad.fechaFin}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun EmptyState(onAction: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Text("No hay actividades registradas", color = Color.Gray)
        TextButton(onClick = onAction) { Text("Limpiar filtros o buscar") }
    }
}

@Composable
fun ErrorState(mensaje: String, onReintentar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Text(mensaje, color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onReintentar, modifier = Modifier.padding(top = 16.dp)) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Sincronizar")
        }
    }
}
