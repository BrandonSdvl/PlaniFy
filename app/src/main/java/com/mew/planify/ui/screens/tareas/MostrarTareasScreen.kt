package com.mew.planify.ui.screens.tareas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.entities.TareaEntity
import com.mew.planify.ui.common.LoadingIndicator
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale




@RequiresApi(Build.VERSION_CODES.O)
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

//    val tareasPendientes = tareas.filter { it.estatus == "Pendiente" && it.fechaEntrega?.toInstant()
//        ?.atZone(
//            ZoneId.systemDefault())?.toLocalDate()?.isBefore(LocalDate.now()) ?: false
//    }
    val tareasPendientes = remember(tareas) { tareas.filter { it.estatus == "Pendiente" && it.fechaEntrega?.toInstant()?.atZone(
            ZoneId.systemDefault())?.toLocalDate()?.isBefore(LocalDate.now()) ?: false} }

    val tareasCompletadas = tareas.filter { it.estatus == "Completada" }
    val tareasVencidas = tareas.filter { it.fechaEntrega?.toInstant()
        ?.atZone(
            ZoneId.systemDefault())?.toLocalDate()?.isBefore(LocalDate.now()) ?: false }

    val pendienteExpanded = remember { mutableStateOf(true) }
    val completadaExpanded = remember { mutableStateOf(true) }
    val vencidaExpanded = remember { mutableStateOf(true) }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(tareas.isEmpty() && !loading){
                    Text(
                        text = "No se ha registrado ninguna tarea",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                if (tareas.isNotEmpty()) {
                    Text("Tareas pendientes",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )

                    HorizontalDivider()
                    if(!tareas.any() { it.estatus == "Pendiente" || it.estatus == "En progreso" }){
                        Text(
                            text = "No hay tareas pendientes",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    LazyColumn {
                            items(tareas.filter { it.estatus == "Pendiente" || it.estatus == "En progreso"}) { tarea ->
                            TareaItem(
                                tarea = tarea,
                                onClick = { onTareaClick(tarea.id) },
                                viewModel = viewModel,
                            )
                        }
                    }

                    Text("Tareas Finalizadas ",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    )

                    HorizontalDivider()
                    if(!tareas.any() { it.estatus == "Finalizada" }){
                        Text(
                            text = "No hay tareas finalizadas",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    LazyColumn {
                        items(tareas.filter { it.estatus == "Finalizada" }) { tarea ->
                            TareaItem(
                                tarea = tarea,
                                onClick = { onTareaClick(tarea.id) },
                                viewModel = viewModel,
                            )
                        }
                    }

                    Text("Tareas Vencidas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    )

                    HorizontalDivider()
                    if(!tareas.any() {
                            it.fechaEntrega?.toInstant()
                                ?.atZone(
                                    ZoneId.systemDefault())?.toLocalDate()?.isBefore(LocalDate.now()) == true
                        }){
                        Text(
                            text = "No hay tareas vencidas",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    LazyColumn {
                        items(tareas.filter {it.fechaEntrega?.toInstant()
                            ?.atZone(
                                ZoneId.systemDefault())?.toLocalDate()?.isBefore(LocalDate.now()) ?: false}) {
                                tarea ->
                            TareaItem(tarea = tarea, onClick = { onTareaClick(tarea.id) }, viewModel = viewModel)
                        }
                    }
                }
            }
        },
        bottomBar = navigator
    )
}

@Composable
fun TareaItem(
    tarea: TareaEntity,
    onClick: () -> Unit,
    viewModel: TareaViewModel,
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = when (tarea.estatus) {
        "Finalizada" -> MaterialTheme.colorScheme.surface.copy(alpha = 0.1f) // Fondo tenue para tareas finalizadas
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.2f) // Fondo tenue para tareas pendientes
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp), // Bordes redondeados
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh // Color de fondo tenue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 15.dp, start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Asegura que los elementos estén centrados verticalmente
        ) {
            // Checkbox en la izquierda
            Checkbox(
                checked = tarea.estatus == "Finalizada", // Estado del Checkbox,
                onCheckedChange = {
                    viewModel.setTarea(tarea.copy(estatus = if (tarea.estatus == "Pendiente" || tarea.estatus == "En progreso") "Finalizada" else "Pendiente"))
                    viewModel.insertOrUpdate()
                } // Actualiza el estado del Checkbox
            )

            Column(
                modifier = Modifier
                    .weight(1f) // Hace que la columna use espacio proporcionalmente
                    .padding(start = 8.dp) // Espacio entre el Checkbox y el contenido
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .then(
                            if (tarea.estatus == "Finalizada") Modifier
                                .then(Modifier.graphicsLayer { alpha = 0.5f }) // Opcional: opacidad
                                .then(Modifier.padding(end = 4.dp)) else Modifier // Si la tarea está finalizada, aplica el tachado
                        ),
                    textDecoration = if (tarea.estatus == "Finalizada") TextDecoration.LineThrough else TextDecoration.None // Tachado si está finalizada
                )
                Text(
                    text = tarea.descripcion,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDarkMode) Color.Gray else Color.DarkGray
                    ),
                    overflow = TextOverflow.Ellipsis, // Trunca el texto con "..."
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .then(
                            if (tarea.estatus == "Finalizada") Modifier
                                .then(Modifier.graphicsLayer {
                                    alpha = 0.5f
                                }) else Modifier // Si está completada, aplica opacidad
                        ),
                    textDecoration = if (tarea.estatus == "Finalizada") TextDecoration.LineThrough else TextDecoration.None // Tachado si está finalizada
                )

                Text(
                    text = "Entrega: ${
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(tarea.fechaEntrega)
                    }",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Light,
                        color = if (isDarkMode) Color.LightGray else Color.Gray
                    )
                )
            }

            Column(
                modifier = Modifier.padding(start = 8.dp), // Espacio entre el contenido y la prioridad
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = when (tarea.prioridad) {
                                "Alta" -> Color(0xD5FCB9B9)
                                "Media" -> Color(0xFFFDECC8)
                                "Baja" -> Color(0xFFDBEDDB)
                                else -> Color(0xFFCCCCCC)
                            },
                            shape = RoundedCornerShape(50) // Borde redondeado (elipse)
                        )
                        .padding(horizontal = 16.dp, vertical = 5.dp) // Espaciado interno
                ) {
                    Text(
                        text = tarea.prioridad,
                        color = when (tarea.prioridad) {
                            "Alta" -> Color(0xFF3D2B2B)  // Color oscuro para "Alta"
                            "Media" -> Color(0xFF6A4E2F)  // Color más oscuro para "Media"
                            "Baja" -> Color(0xFF4A7043)  // Color más oscuro para "Baja"
                            else -> Color(0xFF7E7E7E)  // Color gris para otro caso
                        },
                        style = MaterialTheme.typography.bodySmall, // Ajusta el estilo según tu diseño
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                if (tarea.estatus == "En progreso") {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFBBDEFB), // Fondo azul claro
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "En progreso",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1) // Texto azul oscuro
                            )
                        )
                    }
                }
            }
        }
    }
}
