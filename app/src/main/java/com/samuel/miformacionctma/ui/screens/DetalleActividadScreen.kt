package com.samuel.miformacionctma.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.components.OfflineIndicator
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividadScreen(id: Long, viewModel: AppViewModel, navController: NavController) {
    val context = LocalContext.current
    val actividades by viewModel.actividades.collectAsState()
    val actividad = actividades.find { it.id == id }
    val evidencias by viewModel.getEvidencias(id).collectAsState(initial = emptyList())
    val userRole by viewModel.userRole.collectAsState()
    val selectedMedia by viewModel.selectedMedia.collectAsState()

    var uriCamaraTemporal by remember { mutableStateOf<Uri?>(null) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Text(text = actividad.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(onClick = {}, label = { Text(actividad.prioridad.name) })
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(if (actividad.progreso == 100) "COMPLETADA" else "EN PROCESO") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = if (actividad.progreso == 100) Color(0xFF39A900) else Color.Unspecified
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Descripción", fontWeight = FontWeight.Bold)
                Text(text = actividad.descripcion ?: "Sin descripción adicional.")
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (userRole == "LEARNER") {
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
                                                .padding(vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                modifier = Modifier.fillMaxSize(),
                                                color = Color.Black.copy(alpha = 0.05f),
                                                shape = MaterialTheme.shapes.small
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text("Vista previa cargada", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                                    Text(media.nombre, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Detalles:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    Text(text = "Nombre: ${media.nombre}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Tipo MIME: ${media.mimeType} | Tamaño: ${String.format("%.2f", media.tamanoBytes / (1024.0 * 1024.0))} MB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    
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
                                            viewModel.guardarYEnviarEvidenciaMultimedia(actividad.id)
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
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = evidencia.fechaEntrega.toString().replace("T", " "), style = MaterialTheme.typography.labelSmall)
                                Badge(
                                    containerColor = when(evidencia.estadoSincronizacion) {
                                        "SINCRONIZADA" -> Color(0xFF39A900)
                                        "SUBIENDO" -> Color(0xFFFFF3CD)
                                        "FALLIDA" -> Color.Red
                                        else -> Color.Gray
                                    }
                                ) {
                                    Text(evidencia.estadoSincronizacion, color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                                if (!evidencia.isSynced) OfflineIndicator()
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Archivo: " + evidencia.nombreArchivo, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "URI Local: " + evidencia.evidenciaUri, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            if (evidencia.url.isNotEmpty()) {
                                Text(text = "URL Remota: " + evidencia.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                            }
                            
                            if (evidencia.estadoSincronizacion == "FALLIDA") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.reintentarSincronizacionEvidencia(evidencia.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("REINTENTAR ENVÍO", style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            if (evidencia.comentarioAprendiz != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Comentario del Instructor:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        Text(evidencia.comentarioAprendiz, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
