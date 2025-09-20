package com.tuapp.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavRoot() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "orders") {
        composable("orders") {
            OrdersScreen(
                onNewOrder = { id -> nav.navigate("order/$id") },
                onOpen = { id -> nav.navigate("order/$id") },
                onOpenPrices = { nav.navigate("prices") }
            )
        }
        composable("order/{id}") {
            // Placeholder sin padding/Modifier/dp
            OrderDetailScreen_Placeholder(onBack = { nav.popBackStack() })
        }
        composable("prices") {
            PriceReferenceScreen(
                onBack = { nav.popBackStack() },
                onCreateFromPrices = { newOrderId ->
                    nav.popBackStack()
                    nav.navigate("order/$newOrderId")
                }
            )
        }
    }
}

/** Placeholder temporal muy simple para que compile. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailScreen_Placeholder(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de orden") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { _ ->
        Text("Pantalla detalle (placeholder)")
    }
}
