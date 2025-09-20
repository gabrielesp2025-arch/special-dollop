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
fun PriceReferenceScreen(
    onBack: () -> Unit,
    onCreateFromPrices: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }

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
            // Ruedas
            PriceItem("Ruedas", "Montaje y equilibrado (x neumático)", 10.0, 20.0),
            PriceItem("Ruedas", "Alineación básica", 30.0, 60.0),
            // Diagnosis / clima
            PriceItem("Diagnosis", "Lectura/borra de fallos (OBD)", 20.0, 40.0),
            PriceItem("Clima", "Carga/revisión A/C", 50.0, 120.0),
            // Trabajos grandes (mano de obra orientativa)
            PriceItem("Mecánica", "Kit distribución (mano de obra)", 200.0, 500.0),
            PriceItem("Mecánica", "Embrague (mano de obra)", 200.0, 600.0)
        )
    }
    var items by remember { mutableStateOf(initial.map { it.copy() }) }

    // IVA editable (por defecto 21%)
    var ivaText by remember { mutableStateOf("21.0") }
    val ivaPct = ivaText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 21.0

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
    val minConIva = minTotal * (1 + ivaPct / 100.0)
    val maxConIva = maxTotal * (1 + ivaPct / 100.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarifario / Presupuesto") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } },
                actions = {
                    val selected = items.filter { it.selected }
                    val canCreate = selected.isNotEmpty()
                    TextButton(enabled = canCreate, onClick = {
                        val lines = selected.map {
                            val q = max(1, it.qty)
                            val mid = (it.minEUR + it.maxEUR) / 2.0
                            Triple("${it.category} · ${it.name}", q, mid)
                        }
                        val newId = repo.newOrderFromLines(lines, ivaPct) // 👈 guardamos el IVA elegido
                        onCreateFromPrices(newId)
                    }) { Text("Crear OT") }
                }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Bloque IVA + resumen
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row
