package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.miformacionctma.ui.AppViewModel
import com.samuel.miformacionctma.ui.DashboardData

@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val stats by viewModel.dashboardStats.collectAsState()
    val userName by viewModel.userName.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Hola, $userName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Bienvenido a tu panel de formación", color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            StatCard(
                title = "Progreso General",
                value = "${stats.progresoGeneral}%",
                subtitle = "${stats.completadas} de ${stats.totalActividades} actividades completadas",
                progress = stats.progresoGeneral / 100f
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                SmallStatCard(
                    title = "Pendientes",
                    value = stats.pendientes.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF39A900)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallStatCard(
                    title = "Vencidas",
                    value = stats.vencidas.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(text = "Resumen de Actividades", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Aquí se podrían listar las actividades urgentes (HU-08)
    }
}

@Composable
fun StatCard(title: String, value: String, subtitle: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF39A900)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun SmallStatCard(title: String, value: String, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
