package com.tuapp.ui

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuapp.data.FileRepo
import com.tuapp.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNewOrder: (Long) -> Unit,
    onOpen: (Long) -> Unit,
    onOpenPrices: () -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }
    var orders by remember { mutableStateOf(emptyList<Order>()) }

    LaunchedEffect(Unit) { orders = repo.listOrders() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Órdenes · Taller") },
                actions = { TextButton(onClick = onOpenPrices) { Text("Tarifario") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val id = repo.newOrder()
                orders = repo.listOrders()
                onNewOrder(id)
            }) { Icon(Icons.Default.Add, contentDescription = null) }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            if (orders.isEmpty()) Text("No hay órdenes. Pulsa + para crear la primera.")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(orders) { o ->
                    Card(Modifier.fillMaxWidth().clickable { onOpen(o.id) }) {
                        Column(Modifier.padding(12.dp)) {
                            Text("OT #${o.id}", style = MaterialTheme.typography.titleMedium)
                            Text("Cliente: ${o.customer.name}")
                            Text("Vehículo: ${o.vehicle.brand} ${o.vehicle.model} (${o.vehicle.plate})")
                            Text("Tarifa: €${"%.2f".format(o.baseHourlyRate)} · IVA: ${"%.1f".format(o.vatPct)}%")
                            Text("Total (con IVA): €${"%.2f".format(o.totalWithVat)}")
                        }
                    }
                }
            }
        }
    }
}
