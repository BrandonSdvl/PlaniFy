package com.mew.planify.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun CopyToClipboard(texto: String, contexto: Context) {
    val clipboardManager = contexto.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Texto copiado", texto)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(contexto, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
}