package com.tuapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Modelo simple de referencia de precio
data class PriceReference(
    val job: String,
    val priceRange: String
)

// Datos de ejemplo
val priceReferences = listOf(
    PriceReference("Cambio de aceite", "50€ – 100€"),
    PriceReference("Revisión básica", "80€ – 150€"),
    PriceReference("Cambio de frenos (pastillas)", "120€ – 250€"),
    PriceReference("Cambio de batería", "80€ – 180€"),
    PriceReference("Instalación eléctrica mínima", "20€ – 50€"),
    PriceReference("Instalación eléctrica media", "50€ – 100€"),
    PriceReference("Instalación eléctrica completa", "100€ – 500€"),
    PriceReference("Cambio de correa distribución", "400€ – 900€"),
    PriceReference("Cambio de neumáticos (juego 4)", "250€ – 600€"),
    PriceReference("Alineación y equilibrado", "50€ – 120€")
)

@Composable
fun PriceReferenceScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Referencia de precios") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(priceReferences) { ref ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(ref.job, style = MaterialTheme.typography.titleMedium)
                        Text(ref.priceRange, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
