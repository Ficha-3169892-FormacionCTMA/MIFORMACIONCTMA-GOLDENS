package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var documento by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = documento,
            onValueChange = { documento = it },
            label = { Text("Documento") },
            modifier = Modifier.fillMaxWidth().testTag("et_documento")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("et_password")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (documento == "1001" && password == "Sena2026*") {
                    onLoginSuccess()
                } else {
                    errorStatus = "Credenciales incorrectas"
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("btn_login")
        ) {
            Text("Entrar")
        }

        errorStatus?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("tv_error_status")
            )
        }
    }
}
