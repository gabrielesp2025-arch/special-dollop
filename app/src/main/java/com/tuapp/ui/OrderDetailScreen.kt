package com.tuapp.ui

import android.app.Application
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.tuapp.data.*
import com.tuapp.data.PhotoStage.*
import com.tuapp.util.createImageFile
import com.tuapp.util.generateOrderPdf
import com.tuapp.util.uriForFile
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(orderId: String) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }
    val id = orderId.toLongOrNull() ?: repo.listOrders().firstOrNull()?.id ?: return

    var order by remember { mutableStateOf(repo.getOrder(id)) }

    // Estados para añadir servicio/pieza
    var sDesc by remember { mutableStateOf("") }
    var sHoras by remember { mutableStateOf("") }
    var sTarifa by remember { mutableStateOf(order?.baseHourlyRate?.toString() ?: "35.0") }

    var pCod by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }
    var pCant by remember { mutableStateOf("1") }
    var pPrecio by remember { mutableStateOf("") }

    var iva by remember { mutableStateOf(order?.vatPct?.toString() ?: "21.0") }
    var tarifa by remember { mutableStateOf(order?.baseHourlyRate?.toString() ?: "35.0") }

    // Lanzadores de cámara por etapa
    fun cameraLauncherFor(stage: PhotoStage) = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) {
            pendingPhotoFile?.let { f ->
                repo.addPhoto(id, stage, f.absolutePath)
                order = repo.getOrder(id)
                pendingPhotoFile = null
            }
        } else pendingPhotoFile = null
    }

    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }
    val launchBefore = cameraLauncherFor(BEFORE)
    val launchDuring = cameraLauncherFor(DURING)
    val launchAfter  = cameraLauncherFor(AFTER)

    fun takePhoto(stage: PhotoStage, launch: (Uri) -> Unit) {
        val f = createImageFile(app, "OT${id}_${stage.name}")
        val uri = uriForFile(app, f)
        pendingPhotoFile = f
        launch(uri)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("OT #$id") }) }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            order ?: return@Column

            Text("Cliente: ${order!!.customer.name}")
            Text("Vehículo: ${order!!.vehicle.brand} ${order!!.vehicle.model} (${order!!.vehicle.plate})" +
                    (order!!.vehicle.engineCode?.let { " · Motor: $it" } ?: ""))

            // IVA y tarifa
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(iva, { iva = it }, label = { Text("IVA %") }, modifier = Modifier.width(120.dp))
                OutlinedTextField(tarifa, { tarifa = it }, label = { Text("Tarifa €/h") }, modifier = Modifier.width(140.dp))
                Button(onClick = {
                    repo.updateRates(id, iva.toDoubleOrNull(), tarifa.toDoubleOrNull())
                    order = repo.getOrder(id)
                    sTarifa = order!!.baseHourlyRate.toString()
                }) { Text("Guardar") }
            }

            Divider()

            // Añadir servicio
            Text("Servicios", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(sDesc, { sDesc = it }, label = { Text("Descripción") }, modifier = Modifier.weight(1f))
                OutlinedTextField(sHoras, { sHoras = it }, label = { Text("Horas") }, modifier = Modifier.width(100.dp))
                OutlinedTextField(sTarifa, { sTarifa = it }, label = { Text("€/h") }, modifier = Modifier.width(100.dp))
                Button(onClick = {
                    val h = sHoras.toDoubleOrNull() ?: 0.0
                    val t = sTarifa.toDoubleOrNull() ?: order!!.baseHourlyRate
                    if (sDesc.isNotBlank() && h > 0) {
                        repo.addService(id, sDesc, h, t)
                        order = repo.getOrder(id)
                        sDesc = ""; sHoras = ""
                    }
                }) { Text("Añadir") }
            }
            order!!.services.forEach {
                Text("• ${it.description} – ${it.hours}h × ${"%.2f".format(it.hourlyRate)}€")
            }
            Text("Subtotal mano de obra: €${"%.2f".format(order!!.subtotalServices)}")

            Divider()

            // Añadir pieza
            Text("Piezas", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(pCod, { pCod = it }, label = { Text("Código") }, modifier = Modifier.width(120.dp))
                OutlinedTextField(pDesc, { pDesc = it }, label = { Text("Descripción") }, modifier = Modifier.weight(1f))
                OutlinedTextField(pCant, { pCant = it }, label = { Text("Cant.") }, modifier = Modifier.width(90.dp))
                OutlinedTextField(pPrecio, { pPrecio = it }, label = { Text("€/u") }, modifier = Modifier.width(110.dp))
                Button(onClick = {
                    val cant = pCant.toIntOrNull() ?: 1
                    val precio = pPrecio.toDoubleOrNull() ?: 0.0
                    if (pCod.isNotBlank() && cant > 0) {
                        repo.addPart(id, pCod, if (pDesc.isBlank()) pCod else pDesc, cant, precio, null)
                        order = repo.getOrder(id)
                        pCod = ""; pDesc = ""; pCant = "1"; pPrecio = ""
                    }
                }) { Text("Añadir") }
            }
            order!!.parts.forEach {
                Text("• ${it.code} ${it.description} – ${it.qty} × ${"%.2f".format(it.unitPrice)}€")
            }
            Text("Subtotal piezas: €${"%.2f".format(order!!.subtotalParts)}")

            Divider()

            // Totales
            Text("TOTAL sin IVA: €${"%.2f".format(order!!.totalBase)}", style = MaterialTheme.typography.titleMedium)
            Text("IVA ${"%.1f".format(order!!.vatPct)}%: €${"%.2f".format(order!!.totalBase * order!!.vatPct / 100)}")
            Text("TOTAL con IVA: €${"%.2f".format(order!!.totalWithVat)}", style = MaterialTheme.typography.titleMedium)

            Divider()

            // Fotos por etapa
            Text("Fotos", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { takePhoto(BEFORE, launchBefore::launch) }) { Text("Antes") }
                Button(onClick = { takePhoto(DURING, launchDuring::launch) }) { Text("En marcha") }
                Button(onClick = { takePhoto(AFTER,  launchAfter::launch)  }) { Text("Después") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(order!!.photos.size) { idx ->
                    val ph = order!!.photos[idx]
                    val bm = BitmapFactory.decodeFile(ph.path) ?: return@items
                    Image(bm.asImageBitmap(), contentDescription = null, modifier = Modifier.size(120.dp))
                }
            }

            Divider()

            // Firma
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    // Navega a pantalla de firma sencilla “inline”: abrimos un diálogo/pantalla aparte.
                    // Si prefieres con navegación, puedes integrarla a tu NavHost.
                }) { Text("Abrir pantalla de firma (ver archivo SignatureScreen.kt)") }

                // Generar y compartir PDF
                Button(onClick = {
                    val fresh = repo.getOrder(id)!!
                    val pdf = generateOrderPdf(app, fresh)
                    val uri = FileProvider.getUriForFile(app, "com.tuapp.fileprovider", pdf)
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    app.startActivity(Intent.createChooser(share, "Compartir PDF").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }) { Text("Exportar PDF") }
            }
        }
    }
}
