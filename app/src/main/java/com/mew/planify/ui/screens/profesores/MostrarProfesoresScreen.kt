package com.mew.planify.ui.screens.profesores

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.entities.ProfesorEntity
import com.mew.planify.data.local.entities.TareaEntity
import com.mew.planify.ui.common.LoadingIndicator
import com.mew.planify.ui.viewmodel.ProfesorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostrarProfesoresScreen(
    onCrearProfesorClick: () -> Unit,
    onProfesorClick: (Int) -> Unit,
    viewModel: ProfesorViewModel
) {
    val profesores by viewModel.profesores.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Profesores") },
                actions = {
                    IconButton(onClick = onCrearProfesorClick) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Profesor")
                    }
                }
            )
        },
        content = { padding ->
            LoadingIndicator(enabled = loading)

            Box(modifier = Modifier.fillMaxSize()) {
                if(profesores.isEmpty() && !loading) {
                    Text(
                        text = "No se ha registrado ningún profesor",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(profesores) { profesor ->
                        ProfesorItem(profesor = profesor, onClick = { onProfesorClick(profesor.id) })
                        HorizontalDivider()
                    }
                }

            }
        }
    )
}

@Composable
fun ProfesorItem(profesor: ProfesorEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = profesor.nombre)
            profesor.academia?.let { Text(text = it) }
        }
    }
}