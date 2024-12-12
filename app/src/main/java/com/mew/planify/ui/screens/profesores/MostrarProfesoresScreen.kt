package com.mew.planify.ui.screens.profesores

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mew.planify.data.local.entities.ProfesorEntity
import com.mew.planify.data.local.entities.TareaEntity
import com.mew.planify.ui.common.LoadingIndicator
import com.mew.planify.ui.viewmodel.ProfesorViewModel
import com.mew.planify.utils.CopyToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostrarProfesoresScreen(
    onCrearProfesorClick: () -> Unit,
    onProfesorClick: (Int) -> Unit,
    viewModel: ProfesorViewModel,
    navigator: @Composable () -> Unit
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
                    }
                }

            }
        },
        bottomBar = {
            navigator()
        }
    )
}

@Composable
fun ProfesorItem(profesor: ProfesorEntity, onClick: () -> Unit) {
    val contexto = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp), // Bordes redondeados
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh // Color de fondo tenue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = profesor.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                ),
                modifier = Modifier.weight(0.75f),
                overflow = TextOverflow.Ellipsis
            )

            Column(
                modifier = Modifier.weight(0.25f, fill = false),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = profesor.academia ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary // Resalta la secuencia con el color primario
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Cubículo: ${if(profesor.cubiculo!="") profesor.cubiculo else "N/A"}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }

        HorizontalDivider()

        if(profesor.correo != "") {
            profesor.correo?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { CopyToClipboard(profesor.correo, contexto) }
                        .padding(start = 24.dp, end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Correo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(2f)
                    )
                    IconButton(onClick = {
                        CopyToClipboard(profesor.correo, contexto)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar texto",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        if(profesor.telefono != "") {
            profesor.telefono?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { CopyToClipboard(profesor.telefono, contexto) }
                        .padding(start = 24.dp, end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Teléfono",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(2f)
                    )
                    IconButton(onClick = {
                        CopyToClipboard(profesor.telefono, contexto)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar teléfono",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}