package com.samuel.miformacionctma.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as itemsGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samuel.miformacionctma.model.ActividadFormativa

private val UMBRAL_ANCHO = 600.dp

@Composable
fun ContenidoAdaptable(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        // Se accede explícitamente a maxWidth dentro del BoxWithConstraintsScope
        val anchoPantalla = this.maxWidth

        if (anchoPantalla < UMBRAL_ANCHO) {
            ListaVertical(
                actividades = actividades,
                onActividadClick = onActividadClick
            )
        } else {
            CuadriculaHorizontal(
                actividades = actividades,
                onActividadClick = onActividadClick
            )
        }
    }
}

@Composable
private fun ListaVertical(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = actividades,
            key = { it.id }
        ) { actividad ->
            TarjetaActividad(
                actividad = actividad,
                onClick = onActividadClick
            )
        }
    }
}

@Composable
private fun CuadriculaHorizontal(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsGrid(
            items = actividades,
            key = { it.id }
        ) { actividad ->
            TarjetaActividad(
                actividad = actividad,
                onClick = onActividadClick
            )
        }
    }
}