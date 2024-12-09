package com.mew.planify.ui.screens.profesores

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.mew.planify.ui.common.ConfirmDialog
import com.mew.planify.ui.common.ValidatedTextField
import com.mew.planify.ui.viewmodel.ProfesorViewModel
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProfesorScreen(
    onBack: () -> Unit,
    viewModel: ProfesorViewModel,
    profesorId: Int? = null
) {
    var showDialog by remember {mutableStateOf(false)}
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formState by viewModel.formState

    LaunchedEffect(profesorId) {
        if (profesorId != null) {
            val tarea = viewModel.findById(profesorId).firstOrNull()
            tarea?.let { viewModel.setProfesor(it) }
        }
    }

    val profesor by viewModel.profesor.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (profesorId != null) "Editar profesor" else "Crear profesor") },
                navigationIcon = {
                    IconButton(onClick = {
                        showDialog = true
                    }) { // Botón de regreso en el AppBar
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (profesorId != null) {
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar profesor")
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
                    .padding(16.dp)
            ) {
                Text(if (profesorId != null) "Editar profesor" else "Crear profesor")

                ValidatedTextField(
                    value = profesor.nombre,
                    label = "Nombre",
                    onValueChange = viewModel::onNombreChange,
                    errorMessage = formState.errorNombre
                )

                ValidatedTextField(
                    value = profesor.correo?: "",
                    label = "Correo",
                    onValueChange = viewModel::onCorreoChange,
                    errorMessage = formState.errorCorreo
                )

                ValidatedTextField(
                    value = profesor.telefono?: "",
                    label = "Teléfono",
                    onValueChange = viewModel::onTelefonoChange,
                    errorMessage = formState.errorTelefono
                )

                ValidatedTextField(
                    value = profesor.cubiculo?: "",
                    label = "Cubículo",
                    onValueChange = viewModel::onCubiculoChange,
                    errorMessage = formState.errorCubiculo
                )

                ValidatedTextField(
                    value = profesor.academia?: "",
                    label = "Academia",
                    onValueChange = viewModel::onAcademiaChange,
                    errorMessage = formState.errorAcademia
                )

                ValidatedTextField(
                    value = profesor.nota?: "",
                    label = "Nota",
                    onValueChange = viewModel::onNotaChange,
                    errorMessage = formState.errorNota
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
                            if (viewModel.insertOrUpdate()) onBack()
                        }
                    ) {
                        Text(if (profesorId != null) "Guardar cambios" else "Crear profesor")
                    }
                }

                if (showDialog) ConfirmDialog(
                    title = if (profesorId != null) "Cancelar edición" else "Cancelar creación",
                    message = "¿Estás seguro de que deseas cancelar? Los cambios no se guardarán.",
                    onConfirm = {
                        showDialog = false
                        viewModel.clean()
                        onBack()
                    },
                    onDismiss = { showDialog = false },
                    confirmButtonText = "Salir",
                    dimissButtonText = if (profesorId != null) "Seguir editando" else "Continuar"
                )

                if (showDeleteDialog) ConfirmDialog(
                    title = "Confirmar eliminación",
                    message = "¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.",
                    onConfirm = {
                        profesorId?.let(viewModel::delete)
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