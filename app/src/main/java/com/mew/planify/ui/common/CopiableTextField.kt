package com.mew.planify.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mew.planify.utils.CopyToClipboard

@Composable
fun CopiableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String? = null
) {
    val contexto = LocalContext.current

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth(),
            isError = errorMessage != null,
            trailingIcon = {
                IconButton(onClick = {
                    CopyToClipboard(value, contexto)
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar al portapapeles"
                    )
                }
            }
        )

        // Mostrar mensaje de error debajo del campo de texto si hay uno
        TextErrorMessage(errorMessage)
    }
}
