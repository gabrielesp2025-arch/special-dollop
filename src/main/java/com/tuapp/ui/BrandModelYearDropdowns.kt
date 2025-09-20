package com.tuapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tuapp.data.CarCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDropdown(
    currentBrand: String?,
    onBrandSelected: (String) -> Unit
) {
    val ctx = LocalContext.current
    val brands = remember { CarCatalog.brands(ctx) }

    var expanded by remember { mutableStateOf(false) }
    val selected = currentBrand.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text("Marca") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            brands.forEach { brand ->
                DropdownMenuItem(
                    text = { Text(brand) },
                    onClick = {
                        expanded = false
                        onBrandSelected(brand)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDropdown(
    brand: String?,
    currentModel: String?,
    onModelSelected: (String) -> Unit
) {
    val ctx = LocalContext.current
    val models = remember(brand) {
        if (brand.isNullOrBlank()) emptyList() else CarCatalog.modelsFor(ctx, brand)
    }

    var expanded by remember { mutableStateOf(false) }
    val selected = currentModel.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (models.isNotEmpty()) expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text("Modelo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            placeholder = { Text(if (brand.isNullOrBlank()) "Elige primero una marca" else "") }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model) },
                    onClick = {
                        expanded = false
                        onModelSelected(model)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDropdown(
    brand: String?,
    model: String?,
    currentYear: Int?,
    onYearSelected: (Int) -> Unit
) {
    val ctx = LocalContext.current
    val years = remember(brand, model) {
        if (brand.isNullOrBlank() || model.isNullOrBlank()) emptyList()
        else CarCatalog.yearsFor(ctx, brand, model)
    }

    var expanded by remember { mutableStateOf(false) }
    val selected = currentYear?.toString() ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (years.isNotEmpty()) expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text("Año") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            placeholder = { Text(if (brand.isNullOrBlank() || model.isNullOrBlank()) "Elige marca y modelo" else "") }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            years.forEach { y ->
                DropdownMenuItem(
                    text = { Text(y.toString()) },
                    onClick = {
                        expanded = false
                        onYearSelected(y)
                    }
                )
            }
        }
    }
}
