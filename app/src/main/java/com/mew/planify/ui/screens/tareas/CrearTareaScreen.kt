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
import com.mew.planify.ui.common.ConfirmDialog
import com.mew.planify.ui.common.DropdownField
import com.mew.planify.ui.common.ValidatedTextField
import com.mew.planify.ui.viewmodel.MateriaViewModel
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
    tareaViewModel: TareaViewModel,
    materiaViewModel: MateriaViewModel,
    tareaId: Int? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formState by tareaViewModel.formState

    LaunchedEffect(tareaId) {
        if (tareaId != null) {
            val tarea = tareaViewModel.findById(tareaId).firstOrNull()
            tarea?.let { tareaViewModel.setTarea(it) }
        }
    }

    val tarea by tareaViewModel.tarea.collectAsState()

    val materiasOptions = materiaViewModel.materias.collectAsState(emptyList()).value
    val estatusOptions = listOf("Pendiente", "En progreso", "Finalizada")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var expanded = remember { mutableStateOf(false) }

    calendar.add(Calendar.DAY_OF_MONTH, 1)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time

            tareaViewModel.onFechaEntregaChange(selectedDate)
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
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = (if (tarea.idMateria == null) "Seleccione una opción" else materiaViewModel.findById(tarea.idMateria!!).collectAsState(null).value?.nombre) ?: "Seleccione una opción",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Materia") },
                        isError = formState.errorIdMateria != null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded.value) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        materiasOptions.forEach { materia ->
                            DropdownMenuItem(
                                text = { Text(materia.nombre) },
                                onClick = {
                                    tareaViewModel.onIdMateriaChange(materia.id)
                                    expanded.value = false
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

                ValidatedTextField(
                    value = tarea.titulo,
                    label = "Título",
                    onValueChange = tareaViewModel::onTituloChange,
                    errorMessage = formState.errorTitulo
                )

                ValidatedTextField(
                    value = tarea.descripcion,
                    label = "Descripción",
                    onValueChange = tareaViewModel::onDescripcionChange,
                    errorMessage = formState.errorDescripcion
                )

                DropdownField(
                    label = "Prioridad",
                    options = listOf("Baja", "Media", "Alta"),
                    selectedValue = if (tarea.prioridad.isEmpty()) "Seleccione una opción" else tarea.prioridad,
                    onOptionSelected = { _, option -> tareaViewModel.onPrioridadChange(option) },
                    errorMessage = formState.errorPrioridad
                )

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

                DropdownField(
                    label = "Estatus",
                    options = listOf("Pendiente", "En progreso", "Finalizada"),
                    selectedValue = if (tarea.estatus.isEmpty()) estatusOptions.get(0) else tarea.estatus,
                    onOptionSelected = { _, option -> tareaViewModel.onEstatusChange(option) },
                    errorMessage = formState.errorEstatus
                )

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
                            if (tareaViewModel.insertOrUpdate()) onBack()
                        }
                    ) {
                        Text(if (tareaId != null) "Guardar cambios" else "Crear tarea")
                    }
                }

                if (showDialog) ConfirmDialog(
                    title = if (tareaId != null) "Cancelar edición" else "Cancelar creación",
                    message = "¿Estás seguro de que deseas cancelar? Los cambios no se guardarán.",
                    onConfirm = {
                        showDialog = false
                        tareaViewModel.clean()
                        onBack()
                    },
                    onDismiss = { showDialog = false },
                    confirmButtonText = "Salir",
                    dimissButtonText = if (tareaId != null) "Seguir editando" else "Continuar"
                )

                if (showDeleteDialog) ConfirmDialog(
                    title = "Confirmar eliminación",
                    message = "¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.",
                    onConfirm = {
                        tareaId?.let(tareaViewModel::delete)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false },
                    confirmButtonText ="Eliminar",
                    dimissButtonText = "Cancelar"

                )
            }
        }
    )
}
