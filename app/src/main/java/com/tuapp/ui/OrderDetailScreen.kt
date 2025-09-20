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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

    // ================= VEHÍCULO: imagen =================
    val v = order!!.vehicle

    val resId = com.tuapp.data.carImageResId(
        app,
        brand = v.brand,
        model = v.model,
        year = v.year
    )

    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = "Imagen ${v.brand} ${v.model} ${v.year ?: ""}",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 8.dp),
            contentScale = ContentScale.Fit
        )
    } else {
        Text("Sin imagen disponible para ${v.brand} ${v.model} ${v.year ?: ""}")
    }

    // ================= FOTOS DE TRABAJO =================
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    fun cameraLauncherFor(stage: PhotoStage) =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) {
                pendingPhotoFile?.let { f ->
                    repo.addPhoto(id, stage, f.absolutePath)
                    order = repo.getOrder(id) // refresca
                }
            }
            pendingPhotoFile = null
        }

    val launchBefore = cameraLauncherFor(BEFORE)
    val launchDuring = cameraLauncherFor(DURING)
    val launchAfter = cameraLauncherFor(AFTER)

    fun takePhoto(stage: PhotoStage, launch: (Uri) -> Unit) {
        val f = createImageFile(app, "OT${id}_${stage.name}")
        val uri = uriForFile(app, f)
        pendingPhotoFile = f
        launch(uri)
    }

    // ================= UI =================
    Scaffold(
        topBar = { TopAppBar(title = { Text("OT #$id") }) }
    ) { p ->
        Column(
            Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val o = order ?: return@Column

            Text("Cliente: ${o.customer.name}")
            Text("Vehículo: ${o.vehicle.brand} ${o.vehicle.model} (${o.vehicle.plate})")

            // --------- SELECTORES MARCA / MODELO / AÑO ---------
            Divider()
            Text("Selecciona vehículo", style = MaterialTheme.typography.titleMedium)

            // 1) Marca
            BrandDropdown(currentBrand = o.vehicle.brand) { selectedBrand ->
                repo.updateVehicle(id, brand = selectedBrand, model = "", year = null, engineCode = null)
                order = repo.getOrder(id)
            }

            // 2) Modelo
            ModelDropdown(
                brand = order?.vehicle?.brand,
                currentModel = order?.vehicle?.model
            ) { selectedModel ->
                repo.updateVehicle(id, model = selectedModel, year = null, engineCode = null)
                order = repo.getOrder(id)
            }

            // 3) Año
            YearDropdown(
                brand = order?.vehicle?.brand,
                model = order?.vehicle?.model,
                currentYear = order?.vehicle?.year
            ) { selectedYear ->
                val code = com.tuapp.data.CarCatalog.engineCodeFor(
                    app,
                    brand = order?.vehicle?.brand ?: return@YearDropdown,
                    model = order?.vehicle?.model ?: return@YearDropdown,
                    year = selectedYear
                )
                repo.updateVehicle(id, year = selectedYear, engineCode = code)
                order = repo.getOrder(id)
            }

            val vSel = order!!.vehicle
            Text("Seleccionado: ${vSel.brand} ${vSel.model} ${vSel.year ?: ""}  ${vSel.engineCode?.let { "· Motor: $it" } ?: ""}")
            Divider()
            // ---------------------------------------------------

            // --------- FOTOS ANTES / DURANTE / DESPUÉS ---------
            Text("Fotos", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { takePhoto(BEFORE, launchBefore::launch) }) { Text("Antes") }
                Button(onClick = { takePhoto(DURING, launchDuring::launch) }) { Text("En marcha") }
                Button(onClick = { takePhoto(AFTER, launchAfter::launch) }) { Text("Después") }
            }

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
            // Aquí podrías seguir con piezas, servicios y totales
            Text("TOTAL sin IVA: ———  |  con IVA: ———") // placeholder
        }
    }
}
