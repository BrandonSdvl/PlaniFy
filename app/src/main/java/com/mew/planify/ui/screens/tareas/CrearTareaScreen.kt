package com.mew.planify.ui.screens.tareas

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mew.planify.ui.viewmodel.TareaViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTareaScreen(
    onBack: () -> Unit,
    viewModel: TareaViewModel,
    tareaId: Int? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formState by viewModel.formState

    LaunchedEffect(tareaId) {
        if (tareaId != null) {
            val tarea = viewModel.obtenerTareaPorId(tareaId).firstOrNull()
            tarea?.let { viewModel.setTarea(it) }
        }
    }

    val tarea by viewModel.tarea.collectAsState()

    var materiaExpanded by remember { mutableStateOf(false) } // Estado para controlar el menú desplegable
    val materiaOptions = listOf("Materia 1", "Materia 2", "Materia3")

    var prioridadExpanded by remember { mutableStateOf(false) } // Estado para controlar el menú desplegable
    val prioridadOptions = listOf("Baja", "Media", "Alta")

    var estatusExpanded by remember { mutableStateOf(false) } // Estado para controlar el menú desplegable
    val estatusOptions = listOf("Pendiente", "En progreso", "Finalizada")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    calendar.add(Calendar.DAY_OF_MONTH, 1)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time

            viewModel.onFechaEntregaChange(selectedDate)
            Log.d("DatePicker", "Fecha seleccionada: $selectedDate")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (tareaId != null) "Editar tarea" else "Crear tarea") },
                navigationIcon = {
                    IconButton(onClick = {
                        showDialog = true
                    }) { // Botón de regreso en el AppBar
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (tareaId != null) {
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar tarea")
                        }
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(if (tareaId != null) "Editar tarea" else "Crear tarea")

                ExposedDropdownMenuBox(
                    expanded = materiaExpanded,
                    onExpandedChange = { materiaExpanded = !materiaExpanded } // Cambia el estado del menú desplegable
                ) {
                    OutlinedTextField(
                        value = if (tarea.idMateria == null) "Seleccione una opción" else materiaOptions[tarea.idMateria!!],
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Materia") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = materiaExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = materiaExpanded,
                        onDismissRequest = { materiaExpanded = false } // Cierra el menú al hacer clic fuera
                    ) {
                        materiaOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.onIdMateriaChange(materiaOptions.indexOf(option))
                                    materiaExpanded = false
                                }
                            )
                        }
                    }
                }

                if (formState.errorIdMateria != null) {
                    Text(
                        text = formState.errorIdMateria!!,
                        color = Color.Red,
                    )
                }

                OutlinedTextField(
                    value = tarea.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorTitulo != null
                )

                if (formState.errorTitulo != null) {
                    Text(
                        text = formState.errorTitulo!!,
                        color = Color.Red,
                    )
                }

                OutlinedTextField(
                    value = tarea.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorDescripcion != null
                )

                if (formState.errorDescripcion != null) {
                    Text(
                        text = formState.errorDescripcion!!,
                        color = Color.Red,
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = prioridadExpanded,
                    onExpandedChange = { prioridadExpanded = !prioridadExpanded } // Cambia el estado del menú desplegable
                ) {
                    OutlinedTextField(
                        value = if (tarea.prioridad.isEmpty()) "Seleccione una opción" else tarea.prioridad,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Prioridad") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioridadExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = prioridadExpanded,
                        onDismissRequest = { prioridadExpanded = false } // Cierra el menú al hacer clic fuera
                    ) {
                        prioridadOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.onPrioridadChange(option)
                                    prioridadExpanded = false
                                }
                            )
                        }
                    }
                }

                if (formState.errorPrioridad != null) {
                    Text(
                        text = formState.errorPrioridad!!,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = tarea.fechaEntrega?.let {
                        LocalDate.ofInstant(it.toInstant(), ZoneId.systemDefault()).format(dateFormatter)
                    } ?: "",
                    onValueChange = { /* No acción directa aquí, ya que la fecha se selecciona desde el picker */ },
                    readOnly = true, // Campo de solo lectura
                    label = { Text("Fecha de entrega") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePickerDialog.show()
                        },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Abrir calendario")
                    },
                    isError = formState.errorFechaEntrega != null,
                )

                if (formState.errorFechaEntrega != null) {
                    Text(
                        text = formState.errorFechaEntrega!!,
                        color = Color.Red,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { datePickerDialog.show() }) {
                        Text("Seleccionar fecha")
                    }
                }


                ExposedDropdownMenuBox(
                    expanded = estatusExpanded,
                    onExpandedChange = { estatusExpanded = !estatusExpanded } // Cambia el estado del menú desplegable
                ) {
                    OutlinedTextField(
                        value = if (tarea.estatus.isEmpty()) estatusOptions.get(0) else tarea.estatus,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Estatus") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = estatusExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = estatusExpanded,
                        onDismissRequest = { estatusExpanded = false } // Cierra el menú al hacer clic fuera
                    ) {
                        estatusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.onEstatusChange(option)
                                    estatusExpanded = false
                                }
                            )
                        }
                    }
                }

                if (formState.errorEstatus != null) {
                    Text(
                        text = formState.errorEstatus!!,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showDialog = true }) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if(viewModel.guardarTarea()){
                                onBack()
                            }
                        }
                    ) {
                        Text(if (tareaId != null) "Guardar cambios" else "Crear tarea")
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(if (tareaId != null) "Cancelar edición" else "Cancelar creación") },
                        text = { Text("¿Estás seguro de que deseas cancelar? Los cambios no se guardarán.") },
                        confirmButton = {
                            Button(onClick = {
                                showDialog = false
                            }) {
                                Text(if (tareaId != null) "Seguir editando" else "Continuar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                showDialog = false
                                viewModel.limipiarTarea()
                                onBack()
                            }) {
                                Text("Salir")
                            }
                        }
                    )
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.") },
                        confirmButton = {
                            TextButton(onClick = {
                                if (tareaId != null) {
                                    viewModel.eliminarTarea(tareaId) // Llamada al ViewModel para eliminar la tarea
                                }
                                showDialog = false
                                onBack() // Regresar después de la eliminación
                            }) {
                                Text("Eliminar", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    )
}
