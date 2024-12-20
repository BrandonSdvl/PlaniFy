package com.mew.planify.ui.screens.materias

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mew.planify.R
import com.mew.planify.data.local.entities.MateriaEntity
import com.mew.planify.ui.common.LoadingIndicator
import com.mew.planify.ui.theme.primaryDark
import com.mew.planify.ui.theme.primaryLight
import com.mew.planify.ui.viewmodel.MateriaViewModel
import com.mew.planify.ui.viewmodel.ProfesorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostrarMateriasScreen(
    onCrearMateriaClick: () -> Unit,
    onMateriaClick: (Int) -> Unit,
    materiaViewModel: MateriaViewModel,
    profesorViewModel: ProfesorViewModel,
    navigator: @Composable () -> Unit
) {
    val materias by materiaViewModel.materias.collectAsState()
    val loading by materiaViewModel.loading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = materiaViewModel.snackbarMessage.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                materiaViewModel.clearSnackbarMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  },
                actions = {
                    IconButton(onClick = onCrearMateriaClick) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Materia")
                    }
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = if(isSystemInDarkTheme()) R.drawable.text_logo_dark else R.drawable.text_logo_light),
                        contentDescription = "PlaniFy",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(start = 16.dp)
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            LoadingIndicator(enabled = loading)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    Text(
                        text = "Materias",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                if(materias.isEmpty() && !loading) {
                    item {
                        Text(
                            text = "No se ha registrado ninguna materia",
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }

                items(materias) { materia ->
                    MateriaItem(
                        materia = materia,
                        onClick = { onMateriaClick(materia.id) },
                        profesor = if (materia.idProfesor != null) profesorViewModel.findById(materia.idProfesor).collectAsState(null).value?.nombre ?: "Sin asignar" else "Sin asignar"
                    )
                }
            }
        },
        bottomBar = {
            navigator()
        }
    )
}

@Composable
fun MateriaItem(
    materia: MateriaEntity,
    profesor: String,
    onClick: () -> Unit
) {

    val BlueGray = if(isSystemInDarkTheme()) primaryDark else primaryLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp), // Bordes redondeados
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh // Color de fondo tenue
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(4.dp) // Espaciado entre textos
            ) {
                Text(
                    text = materia.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = profesor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }

            Text(
                text = materia.secuencia,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = BlueGray
                )
            )
        }
    }
}
