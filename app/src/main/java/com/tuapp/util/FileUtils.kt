package com.tuapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createImageFile(context: Context, prefix: String = "IMG"): File {
    val dir = File(context.filesDir, "images").apply { mkdirs() }
    val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    return File(dir, "${prefix}_${ts}.jpg")
}

fun uriForFile(context: Context, file: File): Uri {
    val authority = context.packageName + ".fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}
