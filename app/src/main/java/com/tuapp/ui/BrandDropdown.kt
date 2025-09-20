package com.tuapp.ui

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
    val app = LocalContext.current.applicationContext as Application
    val cars = remember { CarCatalog.load(app) }
    val brands = remember(cars) { cars.map { it.brand }.distinct().sorted() }

    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(currentBrand ?: "") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
                // si escribes manualmente y coincide con una marca, avisa arriba
                if (brands.contains(newValue)) onBrandSelected(newValue)
            },
            label = { Text("Marca") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            brands.forEach { b ->
                DropdownMenuItem(
                    text = { Text(b) },
                    onClick = {
                        text = b
                        onBrandSelected(b)
                        expanded = false
                    }
                )
            }
        }
    }
}
