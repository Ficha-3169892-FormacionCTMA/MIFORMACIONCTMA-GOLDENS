package com.samuel.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samuel.miformacionctma.ui.ActividadViewModel
import com.samuel.miformacionctma.ui.DetalleActividad
import com.samuel.miformacionctma.ui.ListaActividades
import com.samuel.miformacionctma.ui.formulario.FormularioActividad
import com.samuel.miformacionctma.ui.formulario.FormularioActividadEvento
import com.samuel.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiFormacionCTMATheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: ActividadViewModel = viewModel()
) {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "lista",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("lista") {
                ListaActividades(
                    actividades = viewModel.actividades,
                    onActividadClick = { id ->
                        navController.navigate("detalle/$id")
                    },
                    onAgregarClick = {
                        navController.navigate("formulario")
                    }
                )
            }

            composable("formulario") {
                FormularioActividad(
                    uiState = viewModel.uiState,
                    guardando = viewModel.guardando,
                    onEvento = { evento ->
                        viewModel.onEvento(evento)
                        when (evento) {
                            FormularioActividadEvento.Guardar -> {
                                if (viewModel.uiState.esValido) {
                                    navController.popBackStack()
                                }
                            }
                            FormularioActividadEvento.Cancelar -> {
                                navController.popBackStack()
                            }
                            else -> {}
                        }
                    }
                )
            }

            composable(
                route = "detalle/{actividadId}",
                arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("actividadId") ?: -1L
                val actividad = viewModel.buscarPorId(id)
                DetalleActividad(
                    actividad = actividad,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}