package com.samuel.miformacionctma

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samuel.miformacionctma.ui.actividad.ActividadViewModel
import com.samuel.miformacionctma.ui.formulario.FormularioActividad
import com.samuel.miformacionctma.ui.formulario.FormularioActividadEvento
import com.samuel.miformacionctma.ui.screens.PantallaActividades
import com.samuel.miformacionctma.ui.screens.PantallaDetalleActividad

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation(viewModel: ActividadViewModel = viewModel()) {
    val navController = rememberNavController()
    val actividades by viewModel.actividades.collectAsState()
    val navegarAtras by viewModel.navegarAtras.collectAsState()

    // Manejo de navegación exitosa tras guardar
    LaunchedEffect(navegarAtras) {
        if (navegarAtras) {
            navController.popBackStack()
            viewModel.resetNavegarAtras()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        // Pantalla de Listado (Evidencia Semana 3 evolucionada)
        composable("lista") {
            PantallaActividades(
                actividades = actividades,
                onActividadClick = { actividad ->
                    navController.navigate("detalle/${actividad.id}")
                },
                onAgregarActividad = {
                    navController.navigate("formulario")
                }
            )
        }

        // Pantalla de Formulario (Evidencia Semana 4 - Punto 3 y 4)
        composable("formulario") {
            Scaffold(
                topBar = {
                    @OptIn(ExperimentalMaterial3Api::class)
                    TopAppBar(
                        title = { Text("Nueva Actividad") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                FormularioActividad(
                    uiState = viewModel.formUiState,
                    guardando = viewModel.guardando,
                    onEvento = { evento ->
                        if (evento is FormularioActividadEvento.Cancelar) {
                            navController.popBackStack()
                        } else {
                            viewModel.onFormEvento(evento)
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        // Pantalla de Detalle (Evidencia Semana 4 - Punto 6)
        composable(
            route = "detalle/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("actividadId") ?: -1L
            val actividad = viewModel.obtenerActividadPorId(id)

            PantallaDetalleActividad(
                actividad = actividad,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
