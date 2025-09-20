package com.tuapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
            // Placeholder sin padding/dp/Modifier
            OrderDetailScreen_Min(onBack = { nav.popBackStack() })
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

@Composable
private fun OrderDetailScreen_Min(onBack: () -> Unit) {
    Text("Pantalla detalle (placeholder)")
}
