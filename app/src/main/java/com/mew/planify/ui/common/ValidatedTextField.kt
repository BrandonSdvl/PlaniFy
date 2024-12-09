package com.mew.planify.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ValidatedTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = errorMessage != null
    )
    errorMessage?.let {
        Text(it, color = Color.Red)
    }
}