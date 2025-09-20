package com.tuapp.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuapp.data.FileRepo
import com.tuapp.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }

    // Para simplificar: abre la última orden creada si no pasamos el id explícito
    var order by remember { mutableStateOf<Order?>(repo.listOrders().firstOrNull()) }

    // ---- Campos para añadir servicio ----
    var sDesc by remember { mutableStateOf("") }
    var sHoras by remember { mutableStateOf("") }
    var sTarifa by remember { mutableStateOf("") }

    // ---- Campos para añadir pieza ----
    var pCod by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }
    var pCant by remember { mutableStateOf("1") }
    var pPrecio by remember { mutableStateOf("") }
    var pUrl by remember { mutableStateOf("") }

    // ---- Ajustes de IVA y tarifa ----
    var iva by remember { mutableStateOf(order?.vatPct?.toString() ?: "21.0") }
    var tarifa by remember { mutableStateOf(order?.baseHourlyRate?.toString() ?: "35.0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orden #${order?.id ?: "-"}") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { p ->
        Column(
            Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (order == null) {
                Text("No se encontró la orden.")
                Button(onClick = onBack) { Text("Volver") }
                return@Column
            }

            Text("Cliente: ${order!!.customer.name}")
            Text("Vehículo: ${order!!.vehicle.brand} ${order!!.vehicle.model} (${order!!.vehicle.plate})")

            // Ajustes
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    iva, { iva = it }, label = { Text("IVA %") }, modifier = Modifier.width(120.dp)
                )
                OutlinedTextField(
                    tarifa, { tarifa = it }, label = { Text("Tarifa €/h") }, modifier = Modifier.width(140.dp)
                )
                Button(onClick = {
                    repo.updateRates(order!!.id, iva.toDoubleOrNull(), tarifa.toDoubleOrNull())
                    order = repo.getOrder(order!!.id)
                }) { Text("Guardar") }
            }

            Divider()
            Text("Servicios", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(sDesc, { sDesc = it }, label = { Text("Descripción") }, modifier = Modifier.weight(1f))
                OutlinedTextField(sHoras, { sHoras = it }, label = { Text("Horas") }, modifier = Modifier.width(100.dp))
                OutlinedTextField(sTarifa, { sTarifa = it }, label = { Text("€/h") }, modifier = Modifier.width(100.dp))
                Button(onClick = {
                    val h = sHoras.toDoubleOrNull() ?: 0.0
                    val t = sTarifa.toDoubleOrNull() ?: order!!.baseHourlyRate
                    if (sDesc.isNotBlank() && h > 0) {
                        repo.addService(order!!.id, sDesc, h, t)
                        order = repo.getOrder(order!!.id)
                        sDesc = ""; sHoras = ""; sTarifa = ""
                    }
                }) { Text("Añadir") }
            }
            LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                items(order!!.services) { s ->
                    Text("• ${s.description}: ${s.hours}h × €${"%.2f".format(s.hourlyRate)}")
                }
            }
            Text("Subtotal servicios: €${"%.2f".format(order!!.subtotalServices)}")

            Divider()
            Text("Piezas", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(pCod, { pCod = it }, label = { Text("Código") }, modifier = Modifier.width(120.dp))
                OutlinedTextField(pDesc, { pDesc = it }, label = { Text("Descripción") }, modifier = Modifier.weight(1f))
                OutlinedTextField(pCant, { pCant = it }, label = { Text("Cant.") }, modifier = Modifier.width(90.dp))
                OutlinedTextField(pPrecio, { pPrecio = it }, label = { Text("€/u") }, modifier = Modifier.width(110.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(pUrl, { pUrl = it }, label = { Text("URL proveedor (opcional)") }, modifier = Modifier.weight(1f))
                Button(onClick = {
                    val cant = pCant.toIntOrNull() ?: 1
                    val precio = pPrecio.toDoubleOrNull() ?: 0.0
                    if (pCod.isNotBlank()) {
                        repo.addPart(order!!.id, pCod, if (pDesc.isBlank()) pCod else pDesc, cant, precio, pUrl.ifBlank { null })
                        order = repo.getOrder(order!!.id)
                        pCod = ""; pDesc = ""; pCant = "1"; pPrecio = ""; pUrl = ""
                    }
                }) { Text("Añadir pieza") }
            }
            LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                items(order!!.parts) { p ->
                    Text("• ${p.code} ${p.description}: ${p.qty} × €${"%.2f".format(p.unitPrice)}" +
                            (p.supplierUrl?.let { "  [Proveedor]" } ?: ""))
                }
            }
            Text("Subtotal piezas: €${"%.2f".format(order!!.subtotalParts)}")

            Divider()
            Text("Total con IVA (${order!!.vatPct}%): €${"%.2f".format(order!!.totalWithVat)}")

            Divider()
            Button(onClick = onBack) { Text("Volver") }
        }
    }
}
