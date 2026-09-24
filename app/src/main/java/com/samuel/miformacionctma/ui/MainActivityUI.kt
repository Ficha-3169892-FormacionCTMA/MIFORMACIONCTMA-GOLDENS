package com.samuel.miformacionctma.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.samuel.miformacionctma.ui.screens.*
import com.samuel.miformacionctma.util.UserRoles

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Dashboard)
    data object Actividades : Screen("actividades", "Tareas", Icons.AutoMirrored.Filled.List)
    data object Revisar : Screen("cola_revision", "Revisar", Icons.Default.Assignment)
    data object Bitacora : Screen("bitacora", "Bitácora", Icons.Default.HistoryEdu)
    data object Mas : Screen("mas", "Más", Icons.Default.MoreHoriz)
}

@Composable
fun MainScreen(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val userRole by viewModel.userRole.collectAsState()

    val items = if (UserRoles.isInstructor(userRole)) {
        listOf(Screen.Dashboard, Screen.Actividades, Screen.Revisar, Screen.Bitacora, Screen.Mas)
    } else {
        listOf(Screen.Dashboard, Screen.Actividades, Screen.Bitacora, Screen.Mas)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Dashboard.route, Modifier.padding(innerPadding)) {
            composable(Screen.Dashboard.route) {
                val role by viewModel.userRole.collectAsState()
                if (UserRoles.isInstructor(role)) {
                    InstructorDashboardScreen(viewModel, navController)
                } else {
                    DashboardScreen(viewModel, navController)
                }
            }
            composable(Screen.Actividades.route) { ActividadesScreen(viewModel, navController) }
            composable(Screen.Bitacora.route) { BitacoraScreen(viewModel) }
            composable(Screen.Mas.route) { MasScreen(viewModel, navController) }
            composable("cola_revision") {
                val context = LocalContext.current
                val factory = AppViewModelFactory(context.applicationContext as Application)
                val revisionViewModel: RevisionViewModel = viewModel(factory = factory)
                ColaRevisionScreen(viewModel, revisionViewModel, navController)
            }
            
            // Sub-pantallas
            composable("perfil") { PerfilScreen(viewModel, navController) }
            composable("asistencia") { AsistenciaScreen(viewModel, navController) }
            composable("novedades") { NovedadesScreen(viewModel, navController) }
            composable("novedades_recibidas") { NovedadesRecibidasScreen(viewModel, navController) }
            composable("certificados") { CertificadosScreen(viewModel, navController) }
            composable("calendario") { CalendarioScreen(viewModel, navController) }
            composable("configuracion") { SettingsScreen(viewModel, navController) }
            composable("detalle/{id}") { backStack -> 
                val id = backStack.arguments?.getString("id")?.toLong() ?: 0L
                DetalleActividadScreen(id, viewModel, navController) 
            }
            composable("formulario_actividad") { FormularioActividadScreen(viewModel, navController) }
            composable("revision/{actividadId}") { backStack ->
                val actividadId = backStack.arguments?.getString("actividadId")?.toLong() ?: 0L
                val context = LocalContext.current
                val factory = AppViewModelFactory(context.applicationContext as Application)
                val revisionViewModel: RevisionViewModel = viewModel(factory = factory)
                RevisionEvidenciasScreen(actividadId, revisionViewModel, navController)
            }
        }
    }
}
