package com.mew.planify.ui.screens.horario

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mew.planify.R
import com.mew.planify.data.local.dao.HorarioInfo
import com.mew.planify.ui.theme.primaryDark
import com.mew.planify.ui.theme.primaryLight
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

    val BlueGray = if(isSystemInDarkTheme()) primaryDark else primaryLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = if(isSystemInDarkTheme()) R.drawable.text_logo_dark else R.drawable.text_logo_light),
                        contentDescription = "PlaniFy",
                        modifier = Modifier.size(120.dp).padding(start = 16.dp)
                    )
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 24.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Horario",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    )
                }
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
                    Text(
                        currDay.value,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BlueGray
                        )
                    )


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
                    Text(
                        "No hay clases registradas para este día",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    materias.forEach { materia ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onMateriaClick(materia.id) },
                            shape = RoundedCornerShape(10.dp), // Bordes redondeados
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh // Color de fondo tenue
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Fila de la primera parte de la materia
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        materia.hora_inicio.toString(),
                                        Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        materia.nombre_materia,
                                        Modifier.weight(3f),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        if(materia.edificio =="") "N/A" else materia.edificio,
                                        Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                // Fila de la segunda parte de la materia
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        materia.hora_fin.toString(),
                                        Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = if(materia.nombre_profesor != null) materia.nombre_profesor else "Sin asignar",
                                        Modifier.weight(3f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        if(materia.salon == "") "N/A" else materia.salon,
                                        Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                    }
                }
            }
        },
        bottomBar = { navigator() }
    )
}
