package com.tuapp.ui

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuapp.data.*
import com.tuapp.data.PhotoStage.*
import com.tuapp.util.createImageFile
import com.tuapp.util.uriForFile
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(orderId: String) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }
    val id = orderId.toLongOrNull() ?: repo.listOrders().firstOrNull()?.id ?: return
    var order by remember { mutableStateOf(repo.getOrder(id)) }

    // ---------- CÁMARA: preparación ----------
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    fun cameraLauncherFor(stage: PhotoStage) =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) {
                pendingPhotoFile?.let { f ->
                    repo.addPhoto(id, stage, f.absolutePath) // guarda en JSON
                    order = repo.getOrder(id)                // refresca UI
                }
            }
            pendingPhotoFile = null
        }

    val launchBefore = cameraLauncherFor(BEFORE)
    val launchDuring = cameraLauncherFor(DURING)
    val launchAfter  = cameraLauncherFor(AFTER)

    fun takePhoto(stage: PhotoStage, launch: (Uri) -> Unit) {
        val f = createImageFile(app, "OT${id}_${stage.name}") // crea fichero vacío
        val uri = uriForFile(app, f)                           // genera URI segura
        pendingPhotoFile = f
        launch(uri) // abre la cámara para guardar en ese fichero
    }
    // -----------------------------------------

    Scaffold(
        topBar = { TopAppBar(title = { Text("OT #$id") }) }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val o = order ?: return@Column

            Text("Cliente: ${o.customer.name}")
            Text("Vehículo: ${o.vehicle.brand} ${o.vehicle.model} (${o.vehicle.plate})")
// ---------- Selectores: Marca → Modelo → Año ----------
Divider()
Text("Selecciona vehículo", style = MaterialTheme.typography.titleMedium)

// 1) MARCA
BrandDropdown(currentBrand = o.vehicle.brand) { selectedBrand ->
    // Al cambiar marca, vaciamos modelo/año/motor
    repo.updateVehicle(id, brand = selectedBrand, model = "", year = null, engineCode = null)
    order = repo.getOrder(id)
}

// 2) MODELO (filtrado por marca)
ModelDropdown(
    brand = order?.vehicle?.brand,
    currentModel = order?.vehicle?.model
) { selectedModel ->
    // Al cambiar modelo, vaciamos año/motor
    repo.updateVehicle(id, model = selectedModel, year = null, engineCode = null)
    order = repo.getOrder(id)
}

// 3) AÑO (filtrado por marca+modelo)
YearDropdown(
    brand = order?.vehicle?.brand,
    model = order?.vehicle?.model,
    currentYear = order?.vehicle?.year
) { selectedYear ->
    // Al elegir año, buscamos el código de motor y lo guardamos
    val code = com.tuapp.data.CarCatalog.engineCodeFor(
        app,
        brand = order?.vehicle?.brand ?: return@YearDropdown,
        model = order?.vehicle?.model ?: return@YearDropdown,
        year = selectedYear
    )
    repo.updateVehicle(id, year = selectedYear, engineCode = code)
    order = repo.getOrder(id)
}

// Resumen visual de lo seleccionado
val v = order!!.vehicle
Text("Seleccionado: ${v.brand} ${v.model} ${v.year ?: ""}  ${v.engineCode?.let { "· Motor: $it" } ?: ""}")
Divider()
// ------------------------------------------------------
            Divider()
            Text("Fotos", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { takePhoto(BEFORE, launchBefore::launch) }) { Text("Antes") }
                Button(onClick = { takePhoto(DURING, launchDuring::launch) }) { Text("En marcha") }
                Button(onClick = { takePhoto(AFTER,  launchAfter::launch)  }) { Text("Después") }
            }

            // Miniaturas
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(o.photos) { ph ->
                    val bm = BitmapFactory.decodeFile(ph.path)
                    if (bm != null) {
                        Image(
                            bm.asImageBitmap(),
                            contentDescription = "${ph.stage}",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
            }

            Divider()
            // Aquí puedes mantener el resto de tu UI (servicios, piezas, totales, etc.)
            Text("TOTAL sin IVA: ———  |  con IVA: ———") // placeholder
        }
    }
}
