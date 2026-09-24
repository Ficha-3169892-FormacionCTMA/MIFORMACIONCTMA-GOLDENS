package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel

@Composable
fun DashboardScreen(viewModel: AppViewModel, navController: NavController? = null) {
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
                    icon = Icons.Default.HourglassEmpty,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF39A900)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallStatCard(
                    title = "En Revisión",
                    value = stats.enRevision.toString(),
                    icon = Icons.Default.RateReview,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFF9A825)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                SmallStatCard(
                    title = "Completadas",
                    value = stats.completadas.toString(),
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallStatCard(
                    title = "Reprobadas",
                    value = stats.fallidas.toString(),
                    icon = Icons.Default.Cancel,
                    modifier = Modifier.weight(1f),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                SmallStatCard(
                    title = "Vencidas",
                    value = stats.vencidas.toString(),
                    icon = Icons.Default.EventBusy,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE65100)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallStatCard(
                    title = "Total Asignadas",
                    value = stats.totalActividades.toString(),
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (navController != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("novedades_recibidas") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentLate,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Novedades de Instructores", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Consulta avisos e inasistencias reportadas por instructores", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
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
fun SmallStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
