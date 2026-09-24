package com.samuel.miformacionctma.ui.screens

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.samuel.miformacionctma.data.local.entities.EvidenciaEntity
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.AppViewModelFactory
import com.samuel.miformacionctma.ui.RevisionUiState
import com.samuel.miformacionctma.ui.RevisionViewModel
import com.samuel.miformacionctma.ui.components.OfflineIndicator
import com.samuel.miformacionctma.util.EstadoActividad
import com.samuel.miformacionctma.util.UserRoles
import java.io.File

/**
 * Genera una URI segura usando FileProvider aislada en el subdirectorio de cache /evidencias.
 */
private fun crearUriCamara(context: Context): Uri {
    val directorioEvidencias = File(context.cacheDir, "evidencias")
    if (!directorioEvidencias.exists()) {
        directorioEvidencias.mkdirs()
    }
    val archivo = File(directorioEvidencias, "CAP_EVIDENCIA_${System.currentTimeMillis()}.jpg")
    val autoridad = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, autoridad, archivo)
}

@Composable
private fun resolverModeloEvidencia(evidencia: EvidenciaEntity, viewModel: AppViewModel): String {
    var modeloUrl by remember(evidencia.id, evidencia.evidenciaUri, evidencia.url, evidencia.storagePath) {
        val path = evidencia.evidenciaUri.removePrefix("file://")
        val file = File(path)
        val tieneArchivoLocal = evidencia.evidenciaUri.isNotBlank() && (evidencia.evidenciaUri.startsWith("content://") || (file.exists() && file.length() > 0))

        val inicial = if (tieneArchivoLocal) {
            evidencia.evidenciaUri
        } else if (evidencia.url.isNotBlank()) {
            evidencia.url
        } else {
            ""
        }
        mutableStateOf(inicial)
    }

    LaunchedEffect(evidencia.id, evidencia.storagePath, evidencia.url) {
        val path = evidencia.evidenciaUri.removePrefix("file://")
        val file = File(path)
        val tieneArchivoLocal = evidencia.evidenciaUri.isNotBlank() && (evidencia.evidenciaUri.startsWith("content://") || (file.exists() && file.length() > 0))

        if (!tieneArchivoLocal && evidencia.storagePath.isNotBlank()) {
            val freshSignedUrl = viewModel.getSignedUrlForEvidencia(evidencia.storagePath)
            if (freshSignedUrl.isNotBlank()) {
                modeloUrl = freshSignedUrl
            }
        }
    }

    return modeloUrl
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividadScreen(id: Long, viewModel: AppViewModel, navController: NavController) {
    val context = LocalContext.current
    val factory = remember(context) { AppViewModelFactory(context.applicationContext as Application) }
    val revisionViewModel: RevisionViewModel = viewModel(factory = factory)
    val revisionUiState by revisionViewModel.uiState.collectAsState()

    val actividades by viewModel.actividades.collectAsState()
    val actividadesConEstado by viewModel.actividadesConEstado.collectAsState()
    val actividad = actividades.find { it.id == id }
    val estadoActividad = actividadesConEstado.find { it.first.id == id }?.second ?: EstadoActividad.PENDIENTE
    val evidencias by viewModel.getEvidencias(id).collectAsState(initial = emptyList())
    val userRole by viewModel.userRole.collectAsState()
    val currentUserId by viewModel.userId.collectAsState()
    val selectedMedia by viewModel.selectedMedia.collectAsState()

    var uriCamaraTemporal by remember { mutableStateOf<Uri?>(null) }
    var descripcionEvidencia by remember { mutableStateOf("") }
    var fotoAmpliada by remember { mutableStateOf<String?>(null) }
    var evidenciaDetalleSeleccionada by remember { mutableStateOf<EvidenciaEntity?>(null) }
    var mostrarDialogoBorrarActividad by remember { mutableStateOf(false) }

    LaunchedEffect(actividad?.remoteId, userRole) {
        actividad?.remoteId?.let { remoteId ->
            if (UserRoles.isInstructor(userRole)) {
                revisionViewModel.cargarEntregas(remoteId)
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.cargarMediaDesdeUri(uri)
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exitoso ->
        if (exitoso) {
            uriCamaraTemporal?.let { uri ->
                viewModel.cargarMediaDesdeUri(uri)
            }
        }
    }

    if (actividad == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Actividad no encontrada")
        }
        return
    }

    if (mostrarDialogoBorrarActividad) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrarActividad = false },
            title = { Text("¿Borrar esta actividad?") },
            text = { Text("Esta acción eliminará la actividad de forma permanente tanto localmente como en Supabase.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteActividad(actividad)
                        mostrarDialogoBorrarActividad = false
                        navController.popBackStack()
                    }
                ) {
                    Text("BORRAR", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrarActividad = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (UserRoles.isInstructor(userRole) && actividad.instructorId == currentUserId) {
                        IconButton(onClick = { mostrarDialogoBorrarActividad = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar Actividad", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                item {
                    Text(text = actividad.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SuggestionChip(onClick = {}, label = { Text(actividad.prioridad.name) })
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Hasta: ${actividad.fechaFin}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Descripción", fontWeight = FontWeight.Bold)
                    Text(text = actividad.descripcion ?: "Sin descripción adicional.")
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (UserRoles.isInstructor(userRole)) {
                    item {
                        Text(text = "Entregas de los Aprendices", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    when (val state = revisionUiState) {
                        is RevisionUiState.Cargando -> {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is RevisionUiState.Error -> {
                            item {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = state.mensaje, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { actividad.remoteId?.let { revisionViewModel.cargarEntregas(it) } }) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                        }
                        is RevisionUiState.Contenido -> {
                            if (state.lista.isEmpty()) {
                                item {
                                    Text("Nadie ha entregado evidencia todavía.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                                }
                            } else {
                                items(state.lista, key = { it.asignacionId }) { entrega ->
                                    EntregaRevisionItem(
                                        entrega = entrega,
                                        onAprobar = {
                                            actividad.remoteId?.let { remoteId ->
                                                revisionViewModel.marcarEstado(
                                                    actividadRemoteId = remoteId,
                                                    asignacionId = entrega.asignacionId,
                                                    nuevoEstado = "completada"
                                                )
                                            }
                                        },
                                        onRechazar = {
                                            actividad.remoteId?.let { remoteId ->
                                                revisionViewModel.marcarEstado(
                                                    actividadRemoteId = remoteId,
                                                    asignacionId = entrega.asignacionId,
                                                    nuevoEstado = "fallida"
                                                )
                                            }
                                        },
                                        onRevertir = {
                                            actividad.remoteId?.let { remoteId ->
                                                revisionViewModel.marcarEstado(
                                                    actividadRemoteId = remoteId,
                                                    asignacionId = entrega.asignacionId,
                                                    nuevoEstado = "pendiente"
                                                )
                                            }
                                        },
                                        onVerFoto = { fotoUrl ->
                                            fotoAmpliada = fotoUrl
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                if (UserRoles.isAprendiz(userRole)) {
                    if (estadoActividad == EstadoActividad.COMPLETADA) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Esta entrega ya fue aprobada por tu instructor.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else if (evidencias.isEmpty() || selectedMedia != null) {
                        item {
                            Text(text = "Adjuntar Evidencia Digital", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Captura con la cámara o selecciona de la galería. No se requieren permisos generales.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (selectedMedia == null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("GALERÍA", style = MaterialTheme.typography.labelMedium)
                                    }

                                    Button(
                                        onClick = {
                                            val nuevaUri = crearUriCamara(context)
                                            uriCamaraTemporal = nuevaUri
                                            camaraLauncher.launch(nuevaUri)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("CÁMARA", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }

                            selectedMedia?.let { media ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (media.error != null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        if (media.error != null) {
                                            Text(text = media.error, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextButton(onClick = { viewModel.limpiarMedia() }) {
                                                Text("Limpiar", color = MaterialTheme.colorScheme.error)
                                            }
                                        } else {
                                            Text(text = "Finalidad del archivo:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                            Text(
                                                text = "Este archivo se guardará localmente en tu base de datos y se sincronizará como soporte digital verificable de cumplimiento para la actividad formativa seleccionada.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            if (media.uri != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(180.dp)
                                                        .padding(vertical = 4.dp)
                                                        .clip(MaterialTheme.shapes.small),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AsyncImage(
                                                        model = media.uri,
                                                        contentDescription = "Vista previa de la evidencia",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clickable { fotoAmpliada = media.uri.toString() },
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(text = "Detalles:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                            Text(text = "Nombre: ${media.nombre}", style = MaterialTheme.typography.bodyMedium)
                                            Text(text = "Tipo MIME: ${media.mimeType} | Tamaño: ${String.format("%.2f", media.tamanoBytes / (1024.0 * 1024.0))} MB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                            
                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = descripcionEvidencia,
                                                onValueChange = { descripcionEvidencia = it },
                                                label = { Text("Descripción de la actividad realizada") },
                                                placeholder = { Text("Cuéntanos qué hiciste...") },
                                                modifier = Modifier.fillMaxWidth().height(100.dp)
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        photoPickerLauncher.launch(
                                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Reemplazar", style = MaterialTheme.typography.labelSmall)
                                                }

                                                Button(
                                                    onClick = { viewModel.limpiarMedia() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = null)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Eliminar", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Button(
                                                onClick = {
                                                    viewModel.guardarYEnviarEvidenciaMultimedia(
                                                        actividad.id,
                                                        descripcionEvidencia.ifBlank { null }
                                                    )
                                                    descripcionEvidencia = ""
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A900))
                                            ) {
                                                Icon(Icons.Default.Upload, contentDescription = null)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("CONFIRMAR Y SINCRONIZAR EVIDENCIA")
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    item {
                        Text(text = "Evidencias Enviadas", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (evidencias.isEmpty()) {
                        item {
                            Text("No has enviado evidencias aún.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        items(evidencias) { evidencia ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { evidenciaDetalleSeleccionada = evidencia },
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val imageModel = resolverModeloEvidencia(evidencia, viewModel)
                                    if (imageModel.isNotBlank()) {
                                        AsyncImage(
                                            model = imageModel,
                                            contentDescription = "Evidencia enviada",
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(MaterialTheme.shapes.small),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = evidencia.fechaEntrega.toString().take(16).replace("T", " "),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Badge(
                                                    containerColor = when(evidencia.estadoSincronizacion) {
                                                        "SINCRONIZADA" -> Color(0xFF39A900)
                                                        "SUBIENDO" -> Color(0xFFFFF3CD)
                                                        "FALLIDA" -> Color.Red
                                                        else -> Color.Gray
                                                    }
                                                ) {
                                                    Text(
                                                        text = evidencia.estadoSincronizacion,
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                                if (!evidencia.isSynced) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    OfflineIndicator()
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (evidencia.comentarioAprendiz.isNullOrBlank()) "Evidencia adjunta" else evidencia.comentarioAprendiz,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            evidenciaDetalleSeleccionada?.let { evidencia ->
                var modoEdicion by remember(evidencia) { mutableStateOf(false) }
                var comentarioEditado by remember(evidencia) { mutableStateOf(evidencia.comentarioAprendiz ?: "") }

                AlertDialog(
                    onDismissRequest = {
                        evidenciaDetalleSeleccionada = null
                        modoEdicion = false
                        viewModel.limpiarMedia()
                    },
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (modoEdicion) "Editar Evidencia" else "Detalle de Evidencia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Badge(
                                containerColor = when (evidencia.estadoSincronizacion) {
                                    "SINCRONIZADA" -> Color(0xFF39A900)
                                    "SUBIENDO" -> Color(0xFFFFF3CD)
                                    "FALLIDA" -> Color.Red
                                    else -> Color.Gray
                                }
                            ) {
                                Text(evidencia.estadoSincronizacion, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val originalModel = resolverModeloEvidencia(evidencia, viewModel)
                            val displayModel = if (selectedMedia?.uri != null) selectedMedia!!.uri.toString() else originalModel

                            if (displayModel.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { fotoAmpliada = displayModel }
                                ) {
                                    AsyncImage(
                                        model = displayModel,
                                        contentDescription = "Foto evidencia",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Text(
                                text = "Entregado el: ${evidencia.fechaEntrega.toString().take(16).replace("T", " ")}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (!modoEdicion) {
                                Text(
                                    text = "Comentario del Aprendiz:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (evidencia.comentarioAprendiz.isNullOrBlank()) "Sin comentario adjunto." else evidencia.comentarioAprendiz,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                OutlinedTextField(
                                    value = comentarioEditado,
                                    onValueChange = { comentarioEditado = it },
                                    label = { Text("Descripción / Comentario") },
                                    modifier = Modifier.fillMaxWidth().height(100.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("GALERÍA", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Button(
                                        onClick = {
                                            val nuevaUri = crearUriCamara(context)
                                            uriCamaraTemporal = nuevaUri
                                            camaraLauncher.launch(nuevaUri)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("CÁMARA", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        if (!modoEdicion) {
                            if (estadoActividad != EstadoActividad.COMPLETADA && UserRoles.isAprendiz(userRole)) {
                                Button(
                                    onClick = { modoEdicion = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("EDITAR EVIDENCIA")
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (selectedMedia != null) {
                                        viewModel.guardarYEnviarEvidenciaMultimedia(
                                            actividad.id,
                                            comentarioEditado.ifBlank { null }
                                        )
                                    } else {
                                        viewModel.actualizarComentarioEvidencia(
                                            evidencia.id,
                                            actividad.id,
                                            comentarioEditado.ifBlank { null }
                                        )
                                    }
                                    evidenciaDetalleSeleccionada = null
                                    modoEdicion = false
                                    viewModel.limpiarMedia()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A900))
                            ) {
                                Text("GUARDAR CAMBIOS")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                if (modoEdicion) {
                                    modoEdicion = false
                                    viewModel.limpiarMedia()
                                } else {
                                    evidenciaDetalleSeleccionada = null
                                }
                            }
                        ) {
                            Text(if (modoEdicion) "CANCELAR" else "CERRAR")
                        }
                    }
                )
            }

            fotoAmpliada?.let { urlOuRi ->
                Dialog(
                    onDismissRequest = { fotoAmpliada = null },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = urlOuRi,
                                contentDescription = "Foto ampliada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            IconButton(
                                onClick = { fotoAmpliada = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
