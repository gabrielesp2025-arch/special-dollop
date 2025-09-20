package com.tuapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createImageFile(context: Context, prefix: String = "IMG"): File {
    val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val name = "${prefix}_$time.jpg"
    return File(context.filesDir, name) // guarda dentro de la app
}

fun uriForFile(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "com.tuapp.fileprovider", file)
