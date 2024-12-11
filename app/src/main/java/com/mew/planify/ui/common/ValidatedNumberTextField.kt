package com.mew.planify.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ValidatedNumberTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Filtrar para permitir solo números
            if (input.all { it.isDigit() }) {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = errorMessage != null,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number // Muestra el teclado numérico
        )
    )
    errorMessage?.let {
        Text(it, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
    }
}
