package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.components.OfflineIndicator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovedadesScreen(viewModel: AppViewModel, navController: NavController) {
    val novedades by viewModel.novedades.collectAsState()
    
    var tipo by remember { mutableStateOf("MEDICA") }
    var motivo by remember { mutableStateOf("") }
    val tipos = listOf("MEDICA", "PERMISO", "OTRA")

    var esVariosDias by remember { mutableStateOf(false) }
    var fechaUnica by remember { mutableStateOf<LocalDate?>(null) }
    var fechaInicioRango by remember { mutableStateOf<LocalDate?>(null) }
    var fechaFinRango by remember { mutableStateOf<LocalDate?>(null) }

    var showSinglePicker by remember { mutableStateOf(false) }
    var showRangePicker by remember { mutableStateOf(false) }

    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    val singlePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayUtcMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                return utcTimeMillis >= todayUtcMillis
            }
        }
    )

    val rangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayUtcMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                return utcTimeMillis >= todayUtcMillis
            }
        }
    )

    if (showSinglePicker) {
        DatePickerDialog(
            onDismissRequest = { showSinglePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        singlePickerState.selectedDateMillis?.let { millis ->
                            fechaUnica = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        }
                        showSinglePicker = false
                    }
                ) {
                    Text("ACEPTAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSinglePicker = false }) {
                    Text("CANCELAR")
                }
            }
        ) {
            DatePicker(state = singlePickerState)
        }
    }

    if (showRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = rangePickerState.selectedStartDateMillis
                        val endMillis = rangePickerState.selectedEndDateMillis
                        if (startMillis != null && endMillis != null) {
                            fechaInicioRango = Instant.ofEpochMilli(startMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            fechaFinRango = Instant.ofEpochMilli(endMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        }
                        showRangePicker = false
                    }
                ) {
                    Text("ACEPTAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) {
                    Text("CANCELAR")
                }
            }
        ) {
            DateRangePicker(
                state = rangePickerState,
                title = { Text("Selecciona rango de fechas", modifier = Modifier.padding(16.dp)) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    val fechaValida = if (!esVariosDias) {
        fechaUnica != null
    } else {
        fechaInicioRango != null && fechaFinRango != null
    }

    val formularioValido = motivo.isNotBlank() && fechaValida

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Novedad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Nueva Solicitud", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Tipo de Novedad:", style = MaterialTheme.typography.labelLarge)
            Row {
                tipos.forEach { t ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = tipo == t, onClick = { tipo = t })
                        Text(t)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Duración de la Novedad:", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !esVariosDias,
                    onClick = {
                        esVariosDias = false
                        fechaInicioRango = null
                        fechaFinRango = null
                    },
                    label = { Text("Un solo día") },
                    leadingIcon = if (!esVariosDias) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                FilterChip(
                    selected = esVariosDias,
                    onClick = {
                        esVariosDias = true
                        fechaUnica = null
                    },
                    label = { Text("Varios días") },
                    leadingIcon = if (esVariosDias) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!esVariosDias) {
                val textoFecha = fechaUnica?.format(formatter) ?: ""
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = textoFecha,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de la novedad") },
                        placeholder = { Text("Selecciona el día") },
                        trailingIcon = {
                            IconButton(onClick = { showSinglePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showSinglePicker = true }
                            )
                    )
                }
            } else {
                val textoRango = if (fechaInicioRango != null && fechaFinRango != null) {
                    "${fechaInicioRango!!.format(formatter)} al ${fechaFinRango!!.format(formatter)}"
                } else ""

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = textoRango,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rango de fechas de la novedad") },
                        placeholder = { Text("Selecciona las fechas inicial y final") },
                        trailingIcon = {
                            IconButton(onClick = { showRangePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showRangePicker = true }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo / Explicación") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (formularioValido) {
                        if (!esVariosDias) {
                            val fechaFinal = fechaUnica!!
                            viewModel.addNovedad(tipo, motivo, fechaFinal, null, null)
                        } else {
                            val fechaInicioFinal = fechaInicioRango!!
                            val fechaFinFinal = fechaFinRango!!
                            viewModel.addNovedad(tipo, motivo, fechaInicioFinal, fechaFinFinal, null)
                        }
                        
                        motivo = ""
                        fechaUnica = null
                        fechaInicioRango = null
                        fechaFinRango = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formularioValido,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A900))
            ) {
                Text("ENVIAR REPORTE")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Mis Novedades Reportadas", fontWeight = FontWeight.Bold)
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(novedades) { novedad ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = novedad.fecha.toString(), style = MaterialTheme.typography.labelSmall)
                                Badge(
                                    containerColor = when(novedad.estado) {
                                        "APROBADA" -> Color(0xFF39A900)
                                        "RECHAZADA" -> Color.Red
                                        "REVISION" -> Color(0xFFFFA500)
                                        else -> Color.Gray
                                    }
                                ) {
                                    Text(novedad.estado, color = Color.White)
                                }
                            }
                            Text(text = "Tipo: ${novedad.tipo}", fontWeight = FontWeight.Bold)
                            Text(text = novedad.motivo, style = MaterialTheme.typography.bodySmall)
                            if (!novedad.isSynced) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OfflineIndicator()
                                    Text(" Pendiente de envío", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
