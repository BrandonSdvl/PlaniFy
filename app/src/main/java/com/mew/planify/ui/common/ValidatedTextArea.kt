package com.mew.planify.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ValidatedTextArea(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    maxLines: Int = 5 // Número máximo de líneas visibles
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .height((maxLines * 24).dp) // Altura ajustada al número de líneas
            .verticalScroll(rememberScrollState()), // Habilitar scroll vertical
        isError = errorMessage != null,
        maxLines = maxLines,
        singleLine = false // Permitir múltiples líneas
    )

    TextErrorMessage(errorMessage)
}
