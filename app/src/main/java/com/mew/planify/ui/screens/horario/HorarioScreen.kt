package com.mew.planify.ui.screens.horario

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.dao.HorarioInfo
import com.mew.planify.ui.viewmodel.HorarioViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioScreen(
    horarioViewModel: HorarioViewModel,
    navigator: @Composable () -> Unit,
    onMateriaClick: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val diasOptions = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")

    // Estado para el día actual
    val currDay = remember { mutableStateOf(diasOptions[0]) }

    // Estado para la lista de materias
    val materias = remember { mutableStateListOf<HorarioInfo>() }

    // Actualizar materias al cambiar de día
    LaunchedEffect(key1 = currDay.value) {
        val nuevosHorarios = horarioViewModel.getHorarioInfoByDay(currDay.value)
        materias.clear()
        materias.addAll(nuevosHorarios)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Horario") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selector de días
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón para ir al día anterior
                    IconButton(onClick = {
                        val currentIndex = diasOptions.indexOf(currDay.value)
                        if (currentIndex > 0) {
                            currDay.value = diasOptions[currentIndex - 1]
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Día anterior")
                    }

                    // Texto del día actual
                    Text(currDay.value, style = MaterialTheme.typography.titleLarge)

                    // Botón para ir al día siguiente
                    IconButton(onClick = {
                        val currentIndex = diasOptions.indexOf(currDay.value)
                        if (currentIndex < diasOptions.size - 1) {
                            currDay.value = diasOptions[currentIndex + 1]
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Día siguiente")
                    }
                }

                // Mostrar clases o mensaje vacío
                if (materias.isEmpty()) {
                    Text("No hay clases registradas para este día")
                } else {
                    materias.forEach { materia ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onMateriaClick(materia.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(materia.hora_inicio.toString(), Modifier.weight(1f))
                                Text(materia.nombre_materia, Modifier.weight(2f))
                                Text(materia.edificio, Modifier.weight(1f))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(materia.hora_fin.toString(), Modifier.weight(1f))
                                Text(materia.nombre_profesor, Modifier.weight(2f))
                                Text(materia.salon, Modifier.weight(1f))
                            }

                            // Separador
                            Divider()
                        }
                    }
                }
            }
        },
        bottomBar = { navigator() }
    )
}
