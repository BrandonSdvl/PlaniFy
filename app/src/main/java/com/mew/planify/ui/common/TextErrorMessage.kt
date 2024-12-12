package com.mew.planify.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TextErrorMessage(errorMessage: String?) {
    errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error, // Color de error (rojo)
            style = MaterialTheme.typography.bodySmall // Estilo más pequeño para el mensaje
        )
    }
}