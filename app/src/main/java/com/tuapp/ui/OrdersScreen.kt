package com.tuapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuapp.data.FileRepo
import android.app.Application

@Composable
fun OrdersScreen(onOrderClick: (Long) -> Unit) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }
    var orders by remember { mutableStateOf(repo.listOrders()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Órdenes de trabajo") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newId = repo.newOrder()
                orders = repo.listOrders()
                onOrderClick(newId)
            }) { Text("+") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onOrderClick(order.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Cliente: ${order.customer.name}")
                        Text("Vehículo: ${order.vehicle.brand} ${order.vehicle.model}")
                        Text("Matrícula: ${order.vehicle.plate}")
                    }
                }
            }
        }
    }
}
