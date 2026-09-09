package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.samuel.miformacionctma.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasScreen(viewModel: AppViewModel, navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Más opciones") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            item {
                MenuItem("Mi Perfil", Icons.Default.Person) { navController.navigate("perfil") }
                MenuItem("Control de Asistencia", Icons.Default.QrCode) { navController.navigate("asistencia") }
                MenuItem("Calendario Formativo", Icons.Default.CalendarMonth) { navController.navigate("calendario") }
                MenuItem("Certificados", Icons.Default.CardMembership) { navController.navigate("certificados") }
                MenuItem("Reportar Novedad", Icons.Default.AssignmentLate) { navController.navigate("novedades") }
                HorizontalDivider()
                MenuItem("Configuración y Accesibilidad", Icons.Default.Settings) { navController.navigate("configuracion") }
            }
        }
    }
}

@Composable
fun MenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        modifier = Modifier.clickable { onClick() }
    )
}
