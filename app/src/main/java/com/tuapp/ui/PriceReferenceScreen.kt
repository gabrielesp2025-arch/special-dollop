package com.tuapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.max

data class PriceItem(
    val category: String,
    val name: String,
    var minEUR: Double,
    var maxEUR: Double,
    var qty: Int = 1,
    var selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceReferenceScreen(onBack: () -> Unit) {
    // Catálogo orientativo editable en pantalla (rango España / taller independiente).
    val initial = remember {
        listOf(
            // Aceite y filtros
            PriceItem("Mecánica", "Cambio de aceite básico", 50.0, 100.0),
            PriceItem("Mecánica", "Aceite + filtro de aceite", 70.0, 140.0),
            PriceItem("Mecánica", "Revisión filtros (aire/habitáculo)", 30.0, 80.0),

            // Electricidad
            PriceItem("Electricidad", "Instalación eléctrica mínima", 20.0, 50.0),
            PriceItem("Electricidad", "Instalación eléctrica media", 50.0, 100.0),
            PriceItem("Electricidad", "Instalación eléctrica completa", 100.0, 500.0),
            PriceItem("Electricidad", "Sustitución batería (mano de obra)", 15.0, 40.0),

            // Frenos
            PriceItem("Frenos", "Pastillas (eje)", 40.0, 90.0),
            PriceItem("Frenos", "Discos + pastillas (eje)", 90.0, 180.0),
            PriceItem("Frenos", "Purgado / líquido de frenos", 30.0, 60.0),

            // Neumáticos / alineación
            PriceItem("Ruedas", "Montaje y equilibrado (x neumático)", 10.0, 20.0),
            PriceItem("Ruedas", "Alineación básica", 30.0, 60.0),

            // Diagnosis / climatización
            PriceItem("Diagnosis", "Lectura/borra de fallos (OBD)", 20.0, 40.0),
            PriceItem("Clima", "Carga/revisión A/C", 50.0, 120.0),

            // Distribución / embrague (mano de obra orientativa, depende motor)
            PriceItem("Mecánica", "Kit distribución (mano de obra)", 200.0, 500.0),
            PriceItem("Mecánica", "Embrague (mano de obra)", 200.0, 600.0)
        )
    }

    var items by remember { mutableStateOf(initial.map { it.copy() }) }

    fun totals(): Pair<Double, Double> {
        var minT = 0.0
        var maxT = 0.0
        items.filter { it.selected }.forEach {
            val q = max(1, it.qty)
            minT += it.minEUR * q
            maxT += it.maxEUR * q
        }
        return minT to maxT
    }

    val (minTotal, maxTotal) = totals()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarifario / Presupuesto") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Precios orientativos para taller independiente en España. " +
                "Edita rangos según tu coste/hora y proveedores."
            )

            // Resumen
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Selecciona conceptos y cantidades. Puedes editar mínimos/máximos.")
                    Text("Total estimado: €${"%.2f".format(minTotal)} – €${"%.2f".format(maxTotal)} (mano de obra y operaciones).")
                }
            }

            // Lista
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { it ->
                    Card {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${it.category} · ${it.name}", style = MaterialTheme.typography.titleMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = it.minEUR.toString(),
                                    onValueChange = { v -> it.minEUR = v.toDoubleOrNull() ?: it.minEUR },
                                    label = { Text("€ mínimo") },
                                    modifier = Modifier.width(120.dp)
                                )
                                OutlinedTextField(
                                    value = it.maxEUR.toString(),
                                    onValueChange = { v -> it.maxEUR = v.toDoubleOrNull() ?: it.maxEUR },
                                    label = { Text("€ máximo") },
                                    modifier = Modifier.width(120.dp)
                                )
                                OutlinedTextField(
                                    value = it.qty.toString(),
                                    onValueChange = { v -> it.qty = (v.toIntOrNull() ?: it.qty).coerceAtLeast(1) },
                                    label = { Text("Cantidad") },
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val selected = it.selected
                                val toggleText = if (selected) "Quitar del presupuesto" else "Añadir al presupuesto"
                                Button(onClick = {
                                    it.selected = !selected
                                    // Forzar recomposición
                                    items = items.toList()
                                }) { Text(toggleText) }
                                Text("Línea: €${"%.2f".format(it.minEUR*it.qty)} – €${"%.2f".format(it.maxEUR*it.qty)}")
                            }
                        }
                    }
                }
            }
        }
    }
}
