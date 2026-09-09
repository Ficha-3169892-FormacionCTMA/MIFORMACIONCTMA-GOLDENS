package com.samuel.miformacionctma.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.miformacionctma.ui.AppViewModel

@Composable
fun LoginScreen(viewModel: AppViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("LEARNER") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi Formación CTMA",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF39A900)
        )
        Text(
            text = "Centro de Tecnología de la Manufactura",
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo SENA") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = role == "LEARNER", onClick = { role = "LEARNER" })
            Text("Aprendiz")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = role == "INSTRUCTOR", onClick = { role = "INSTRUCTOR" })
            Text("Instructor")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                if (email.isNotBlank() && password.length >= 6) {
                    viewModel.login(email, role, email.split("@")[0])
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A900)),
            enabled = email.isNotBlank() && password.length >= 6
        ) {
            Text("INGRESAR")
        }
    }
}
