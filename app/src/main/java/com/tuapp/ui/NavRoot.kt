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
        composable("orders") { OrdersScreen(
            onNewOrder = { id -> nav.navigate("order/$id") },
            onOpen = { id -> nav.navigate("order/$id") },
            onOpenPrices = { nav.navigate("prices") }
        ) }
        composable("order/{id}") { Text("Detalle de orden (placeholder)") }
        composable("prices") { PriceReferenceScreen(onBack = { nav.popBackStack() }, onCreateFromPrices = { id -> nav.navigate("order/$id") }) }
    }
}
