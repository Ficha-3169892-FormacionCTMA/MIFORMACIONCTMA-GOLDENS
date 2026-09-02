package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(viewModel: AppViewModel, navController: NavController) {
    val actividades by viewModel.actividades.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario Formativo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            CalendarHeader(currentMonth, onMonthChange = { currentMonth = it })
            CalendarGrid(currentMonth, selectedDate, actividades) { selectedDate = it }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            Text(
                text = "Actividades para el $selectedDate",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
            
            val actividadesDelDia = actividades.filter { it.fechaFin == selectedDate }
            
            if (actividadesDelDia.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No hay actividades programadas", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(actividadesDelDia) { actividad ->
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
fun CalendarHeader(currentMonth: YearMonth, onMonthChange: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Mes Anterior")
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))} ${currentMonth.year}".uppercase(),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Mes Siguiente")
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    actividades: List<com.samuel.miformacionctma.data.local.entities.ActividadEntity>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
    
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        var cellIndex = 0
        for (row in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayNumber = cellIndex - firstDayOfMonth + 1
                    val date = if (dayNumber in 1..daysInMonth) currentMonth.atDay(dayNumber) else null
                    
                    CalendarCell(
                        date = date,
                        isSelected = date == selectedDate,
                        hasActividad = date?.let { d -> actividades.any { it.fechaFin == d } } ?: false,
                        modifier = Modifier.weight(1f),
                        onClick = { date?.let { onDateSelected(it) } }
                    )
                    cellIndex++
                }
            }
        }
    }
}

@Composable
fun CalendarCell(
    date: LocalDate?,
    isSelected: Boolean,
    hasActividad: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                color = if (isSelected) Color(0xFF39A900) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = date != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) Color.White else Color.Unspecified,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                if (hasActividad) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(if (isSelected) Color.White else Color(0xFFFF6B00), shape = MaterialTheme.shapes.extraSmall)
                    )
                }
            }
        }
    }
}
