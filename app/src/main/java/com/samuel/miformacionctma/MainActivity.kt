package com.samuel.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.samuel.miformacionctma.ui.screens.PantallaActividades
import com.samuel.miformacionctma.ui.screens.obtenerActividadesDePrueba

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaActividades(
                        actividades = obtenerActividadesDePrueba()
                    )
                }
            }
        }
    }
}