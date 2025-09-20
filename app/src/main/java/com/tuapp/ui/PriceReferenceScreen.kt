package com.tuapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class PriceItem(val name: String, val minPrice: Int, val maxPrice: Int)

@Composable
fun PriceReferenceScreen() {
    val priceList = listOf(
        PriceItem("Cambio de aceite", 50, 100),
        PriceItem("Instalación mínima de electricidad", 20, 50),
        PriceItem("Instalación media de electricidad", 50, 100),
        PriceItem("Instalación eléctrica completa", 100, 500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Referencia de Precios", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        priceList.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.name, style = MaterialTheme.typography.bodyLarge)
                    Text("Rango: ${item.minPrice}€ - ${item.maxPrice}€")
                }
            }
        }
    }
}
