package com.mew.planify.ui.screens.tareas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mew.planify.ui.viewmodel.TareaViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(
    tareaId: Int,
    onBack: () -> Unit,
    viewModel: TareaViewModel
) {
    val tarea by viewModel.findById(tareaId).collectAsState(initial = null)
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Tarea") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        content = { padding ->
            tarea?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = it.titulo)
                    Text(text = "Materia: ${it.idMateria}")
                    Text(text = "Descripción: ${it.descripcion}")
                    Text(text = "Prioridad: ${it.prioridad}")
                    Text(text = "Fecha de entrega: ${it.fechaEntrega?.let {
                        LocalDate.ofInstant(it.toInstant(), ZoneId.systemDefault()).format(dateFormatter)
                    } ?: ""}")
                    Text(text = "Estatus: ${it.estatus}")
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tarea no encontrada")
                }
            }
        }
    )
}