package com.mew.planify.ui.screens.materias

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.entities.ProfesorEntity
import com.mew.planify.ui.common.ConfirmDialog
import com.mew.planify.ui.common.ValidatedTextField
import com.mew.planify.ui.viewmodel.HorarioViewModel
import com.mew.planify.ui.viewmodel.MateriaViewModel
import com.mew.planify.ui.viewmodel.ProfesorViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMateriaScreen (
    onBack: () -> Unit,
    materiaViewModel: MateriaViewModel,
    profesorViewModel: ProfesorViewModel,
    horarioViewModel: HorarioViewModel,
    idMateria: Int? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formState by materiaViewModel.formState
    val profesorOptions = listOf(ProfesorEntity(nombre = "Sin asignar")) + profesorViewModel.profesores.collectAsState(emptyList()).value

    var expanded = remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()


    LaunchedEffect(key1 = idMateria) {
        if (idMateria == null) {
            materiaViewModel.clean()
            horarioViewModel.clean()
            showDeleteDialog = false
            showDialog = false
        } else {
            val materia = materiaViewModel.findById(idMateria).firstOrNull()
            materia?.let { materiaViewModel.setMateria(it) }

            val hr = materiaViewModel.obtenerMateriaConHorarios(idMateria)
            println("DEBUGGG")

            println(hr)
            println(hr.horarios)
            horarioViewModel.setCurrClases(hr.horarios)
            println(horarioViewModel.currClases.value.size)
            horarioViewModel.setFormState(horarioViewModel.currClases.value.size)
        }
    }


    val materia by materiaViewModel.materia.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (idMateria != null) "Editar materia" else "Crear materia") },
                navigationIcon = {
                    IconButton(onClick = {
                        showDialog = true
                    }) { // Botón de regreso en el AppBar
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (idMateria != null) {
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar materia")
                        }
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(if (idMateria != null) "Editar materia" else "Crear materia")

                ValidatedTextField(
                    value = materia.secuencia,
                    label = "Secuencia",
                    onValueChange = materiaViewModel::onSecuenciaChange,
                    errorMessage = formState.errorSecuencia
                )

                ValidatedTextField(
                    value = materia.nombre,
                    label = "Nombre",
                    onValueChange = materiaViewModel::onNombreChange,
                    errorMessage = formState.errorNombre
                )

                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = (if (materia.idProfesor == null) "Sin asignar" else profesorViewModel.findById(materia.idProfesor!!).collectAsState(null).value?.nombre) ?: "Sin asignar",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profesor") },
                        isError = formState.errorIdProfesor != null,
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
                        profesorOptions.forEachIndexed { index, profesor ->
                            DropdownMenuItem(
                                text = { Text(profesor.nombre) },
                                onClick = {
                                    materiaViewModel.onIdProfesorChange(if (index == 0) null else profesor.id)
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                formState.errorIdProfesor?.let {
                    Text(it, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sección de clases
                Text("Clases", style = MaterialTheme.typography.titleLarge)
                if (horarioViewModel.currClases.collectAsState().value.isEmpty() && horarioViewModel.formState.collectAsState().value.isEmpty()) {
                    Text("No hay clases agregadas", style = MaterialTheme.typography.bodyMedium)
                } else {
                    horarioViewModel.currClases.collectAsState().value.forEachIndexed { index, clase ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Clase ${index + 1}", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(onClick = { horarioViewModel.deleteClase(index) }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Eliminar clase")
                                    }
                                }

                                FormClase(horarioViewModel, index) // Formulario para cada clase
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa todo el ancho disponible
                        .clickable { horarioViewModel.add() } // Acción al hacer clic
                        .padding(16.dp), // Espaciado
                    verticalAlignment = Alignment.CenterVertically, // Alineación vertical
                    horizontalArrangement = Arrangement.Center // Centrado horizontal
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, // Ícono de Material Design para "+"
                        contentDescription = "Agregar", // Descripción accesible
                        tint = MaterialTheme.colorScheme.primary // Color del ícono
                    )
                    Text(
                        text = "Agregar materia ",
                        style = MaterialTheme.typography.bodyMedium // Estilo de texto
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
                            scope.launch {
                                if (
                                    horarioViewModel.validateAll(horarioViewModel.currClases.value) &&
                                    materiaViewModel.insertarMateriaConHorarios(horarioViewModel.currClases.value)
                                ) {
                                    horarioViewModel.clean()
                                    onBack()
                                }
                            }
                        }
                    ) {
                        Text(if (idMateria != null) "Guardar cambios" else "Crear materia")
                    }
                }

                if (showDialog && (materiaViewModel.changed.collectAsState().value || horarioViewModel.changed.collectAsState().value)) ConfirmDialog(
                    title = if (idMateria != null) "Cancelar edición" else "Cancelar creación",
                    message = "¿Estás seguro de que deseas cancelar? Los cambios no se guardarán.",
                    onConfirm = {
                        showDialog = false
                        materiaViewModel.clean()
                        onBack()
                    },
                    onDismiss = { showDialog = false },
                    confirmButtonText = "Salir",
                    dimissButtonText = if (idMateria != null) "Seguir editando" else "Continuar"
                )

                if (showDialog && !materiaViewModel.changed.collectAsState().value && !horarioViewModel.changed.collectAsState().value) {
                    showDialog = false
                    onBack()
                }


                if (showDeleteDialog) ConfirmDialog(
                    title = "Confirmar eliminación",
                    message = "¿Estás seguro de que deseas eliminar esta materia? Esta acción no se puede deshacer.",
                    onConfirm = {
                        idMateria?.let(materiaViewModel::delete)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false },
                    confirmButtonText = "Eliminar",
                    dimissButtonText = "Cancelar"
                )

                if(materiaViewModel.error.collectAsState().value != null) {
                    AlertDialog(
                        onDismissRequest = { materiaViewModel.cleanError() },  // Cierra el diálogo si se toca fuera de él
                        title = {
                            Text(text = "Aviso")
                        },
                        text = {
                            Text(materiaViewModel.error.collectAsState().value!!)
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    materiaViewModel.cleanError()  // Cierra el diálogo al hacer clic en el botón
                                }
                            ) {
                                Text("Continuar")
                            }
                        }
                    )
                }
            }
        }
    )
}