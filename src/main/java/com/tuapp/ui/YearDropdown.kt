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
fun YearDropdown(
    brand: String?,
    model: String?,
    currentYear: Int?,
    onYearSelected: (Int) -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val years = remember(brand, model) {
        if (brand.isNullOrBlank() || model.isNullOrBlank()) emptyList()
        else CarCatalog.yearsFor(app, brand, model)
    }

    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(currentYear?.toString() ?: "") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        if (years.isNotEmpty()) expanded = !expanded
    }) {
        TextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
                newValue.toIntOrNull()?.let { y ->
                    if (years.contains(y)) onYearSelected(y)
                }
            },
            label = { Text("Año") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            enabled = years.isNotEmpty(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            years.forEach { y ->
                DropdownMenuItem(
                    text = { Text(y.toString()) },
                    onClick = {
                        text = y.toString()
                        onYearSelected(y)
                        expanded = false
                    }
                )
            }
        }
    }
}
