package com.mew.planify.ui.screens.tareas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mew.planify.ui.viewmodel.TareaViewModel
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.entities.TareaEntity
import com.mew.planify.ui.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostrarTareasScreen(
    onCrearTareaClick: () -> Unit,
    onTareaClick: (Int) -> Unit,
    viewModel: TareaViewModel,
    navigator: @Composable () -> Unit
) {
    val tareas by viewModel.tareas.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas") },
                actions = {
                    IconButton(onClick = onCrearTareaClick) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Tarea")
                    }
                }
            )
        },
        content = { padding ->
            LoadingIndicator(enabled = loading)

            Box(modifier = Modifier.fillMaxSize()) {
                if(tareas.isEmpty() && !loading){
                    Text(
                        text = "No se ha registrado ninguna tarea",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tareas) { tarea ->
                        TareaItem(tarea = tarea, onClick = { onTareaClick(tarea.id) })
                        HorizontalDivider()
                    }
                }
            }
        },
        bottomBar = navigator
    )
}

@Composable
fun TareaItem(tarea: TareaEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f) // Hace que la columna use espacio proporcionalmente
                .wrapContentWidth(Alignment.Start) // Asegura que el contenido esté alineado a la izquierda
                .widthIn(max = 240.dp) // Limita el ancho máximo de la columna
        ) {
            Text(text = tarea.titulo)
            Text(
                text = tarea.descripcion,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Trunca el texto con "..."
            )
        }
        Text(
            text = tarea.prioridad,
            modifier = Modifier.padding(start = 8.dp) // Asegura espacio entre la columna y el texto de prioridad
        )
    }

}