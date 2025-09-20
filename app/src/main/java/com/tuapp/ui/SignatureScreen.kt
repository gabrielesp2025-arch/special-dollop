package com.tuapp.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuapp.data.FileRepo
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignatureScreen(orderId: Long, onDone: () -> Unit, onCancel: () -> Unit) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }

    var path by remember { mutableStateOf(Path()) }
    var size by remember { mutableStateOf(androidx.compose.ui.geometry.Size(0f, 0f)) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Firma del cliente") })
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> path.moveTo(offset.x, offset.y) },
                            onDrag = { change, _ -> path.lineTo(change.position.x, change.position.y) }
                        )
                    },
                content = {
                    Canvas(modifier = Modifier.fillMaxSize().onSizeChanged { size = androidx.compose.ui.geometry.Size(it.width.toFloat(), it.height.toFloat()) }) {
                        drawPath(path = path, color = androidx.compose.ui.graphics.Color.Black, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { path = Path() }) { Text("Borrar") }
                Button(onClick = {
                    // Guardar a PNG
                    val bmp = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp); canvas.drawColor(Color.WHITE)
                    val ab = androidx.compose.ui.graphics.asAndroidPath(path)
                    val pnt = android.graphics.Paint().apply { color = Color.BLACK; style = android.graphics.Paint.Style.STROKE; strokeWidth = 6f; isAntiAlias = true }
                    canvas.drawPath(ab, pnt)

                    val file = File(app.filesDir, "SIGN_${orderId}.png")
                    FileOutputStream(file).use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out) }
                    repo.saveSignature(orderId, file.absolutePath)
                    onDone()
                }) { Text("Guardar firma") }
                OutlinedButton(onClick = onCancel) { Text("Cancelar") }
            }
        }
    }
}
