package com.tuapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.tuapp.data.CarCatalog

// ---------- DROPDOWN DE MARCA ----------
@Composable
fun BrandDropdown(
    currentBrand: String?,
    onBrandSelected: (String) -> Unit
) {
    val ctx = LocalContext.current
    val brands = remember { CarCatalog.brands(ctx) }

    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(currentBrand ?: "") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Marca") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            brands.forEach { brand ->
                DropdownMenuItem(
                    text = { Text(brand) },
                    onClick = {
                        selected = brand
                        expanded = false
                        onBrandSelected(brand)
                    }
                )
            }
        }
    }
}

// ---------- DROPDOWN DE MODELO ----------
@Composable
fun ModelDropdown(
    brand: String?,
    currentModel: String?,
    onModelSelected: (String) -> Unit
) {
    if (brand == null || brand.isBlank()) return

    val ctx = LocalContext.current
    val models = remember(brand) { CarCatalog.models(ctx, brand) }

    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(currentModel ?: "") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Modelo") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model) },
                    onClick = {
                        selected = model
                        expanded = false
                        onModelSelected(model)
                    }
                )
            }
        }
    }
}

// ---------- DROPDOWN DE AÑO ----------
@Composable
fun YearDropdown(
    brand: String?,
    model: String?,
    currentYear: Int?,
    onYearSelected: (Int) -> Unit
) {
    if (brand == null || brand.isBlank() || model == null || model.isBlank()) return

    val ctx = LocalContext.current
    val years = remember(brand to model) { CarCatalog.years(ctx, brand, model) }

    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(currentYear?.toString() ?: "") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Año") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        selected = year.toString()
                        expanded = false
                        onYearSelected(year)
                    }
                )
            }
        }
    }
}
