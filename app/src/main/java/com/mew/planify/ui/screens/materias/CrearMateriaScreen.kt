package com.mew.planify.ui.screens.materias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.entities.ProfesorEntity
import com.mew.planify.ui.common.ConfirmDialog
import com.mew.planify.ui.common.ValidatedTextField
import com.mew.planify.ui.viewmodel.MateriaViewModel
import com.mew.planify.ui.viewmodel.ProfesorViewModel
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMateriaScreen (
    onBack: () -> Unit,
    materiaViewModel: MateriaViewModel,
    profesorViewModel: ProfesorViewModel,
    idMateria: Int? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formState by materiaViewModel.formState
    val profesorOptions = listOf(ProfesorEntity(nombre = "Sin asignar")) + profesorViewModel.profesores.collectAsState(emptyList()).value

    var expanded = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = idMateria) {
        if (idMateria == null) {
            materiaViewModel.clean()
            showDeleteDialog = false
            showDialog = false
        } else {
            val materia = materiaViewModel.findById(idMateria).firstOrNull()
            materia?.let { materiaViewModel.setMateria(it) }
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
                            if (materiaViewModel.insertOrUpdate()) onBack()
                        }
                    ) {
                        Text(if (idMateria != null) "Guardar cambios" else "Crear materia")
                    }
                }

                if (showDialog && materiaViewModel.changed.collectAsState().value) ConfirmDialog(
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

                if (showDialog && !materiaViewModel.changed.collectAsState().value) {
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
            }
        }
    )
}