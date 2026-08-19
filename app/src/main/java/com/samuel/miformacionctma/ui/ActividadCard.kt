package com.samuel.miformacionctma.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.model.Prioridad

@Composable
fun ActividadCard(
    actividad: ActividadFormativa,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                PrioridadBadge(actividad.prioridad)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            actividad.descripcion?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Progreso: ${actividad.progreso}%",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { actividad.progreso / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Vence en: ${actividad.diasRestantes} días",
                style = MaterialTheme.typography.labelSmall,
                color = if (actividad.diasRestantes <= 2) Color.Red else Color.Unspecified
            )
        }
    }
}

@Composable
fun PrioridadBadge(prioridad: Prioridad) {
    val color = when (prioridad) {
        Prioridad.ALTA -> Color(0xFFFFBABA)
        Prioridad.MEDIA -> Color(0xFFFFF3BA)
        Prioridad.BAJA -> Color(0xFFBAFFC9)
    }
    val textColor = Color.Black
    
    Card(
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(
            text = prioridad.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}