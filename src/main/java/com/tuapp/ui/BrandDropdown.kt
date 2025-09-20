package com.tuapp.ui

import android.app.Application
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import com.tuapp.data.CarCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDropdown(
    currentBrand: String?,
    onBrandSelected: (String) -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val brands = remember { CarCatalog.brands(app) }

    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(currentBrand ?: "") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
                if (brands.contains(newValue)) onBrandSelected(newValue)
            },
            label = { Text("Marca") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
