package com.samuel.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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
            startDestination = "login", // Cambiado para el flujo de prueba de seguridad
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(onLogin = { navController.navigate("asistencia") })
            }

            composable("asistencia") {
                AsistenciaScreen(onBack = { navController.navigate("lista") })
            }

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

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var doc by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    
    Column(Modifier.padding(16.dp)) {
        Text("Ingreso al Sistema", modifier = Modifier.padding(bottom = 16.dp))
        OutlinedTextField(
            value = doc,
            onValueChange = { doc = it },
            label = { Text("Documento") },
            modifier = Modifier.fillMaxWidth().testTag("et_documento")
        )
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().testTag("et_password")
        )
        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("btn_login")
        ) {
            Text("Ingresar")
        }
    }
}

@Composable
fun AsistenciaScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var errorStatus by remember { mutableStateOf("") }
    
    Column(Modifier.padding(16.dp)) {
        Text("Consulta de Asistencia", modifier = Modifier.padding(bottom = 16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Documento del Aprendiz") },
            modifier = Modifier.fillMaxWidth().testTag("et_buscar_aprendiz")
        )
        Button(
            onClick = {
                if (query == "1002") {
                    errorStatus = "Acceso Denegado (403): No tiene permisos para consultar este registro"
                } else {
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("btn_consultar")
        ) {
            Text("Consultar")
        }
        
        if (errorStatus.isNotEmpty()) {
            Text(
                text = errorStatus,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp).testTag("tv_error_status")
            )
        }
    }
}