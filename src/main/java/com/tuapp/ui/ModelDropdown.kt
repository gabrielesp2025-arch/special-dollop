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
fun ModelDropdown(
    brand: String?,
    currentModel: String?,
    onModelSelected: (String) -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val models = remember(brand) {
        if (brand.isNullOrBlank()) emptyList() else CarCatalog.modelsForBrand(app, brand)
    }

    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(currentModel ?: "") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        if (models.isNotEmpty()) expanded = !expanded
    }) {
        TextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
                if (models.contains(newValue)) onModelSelected(newValue)
            },
            label = { Text("Modelo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            enabled = models.isNotEmpty(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            models.forEach { m ->
                DropdownMenuItem(
                    text = { Text(m) },
                    onClick = {
                        text = m
                        onModelSelected(m)
                        expanded = false
                    }
                )
            }
        }
    }
}
