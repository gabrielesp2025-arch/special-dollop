package com.tuapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createImageFile(context: Context, prefix: String = "IMG"): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "${prefix}_${timeStamp}.jpg"
    val dir = context.filesDir
    return File(dir, fileName)
}

fun uriForFile(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "com.tuapp.fileprovider", file)
fun createImageFile(context: Context, prefix: String = "IMG"): File { ... }
fun uriForFile(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "com.tuapp.fileprovider", file)
