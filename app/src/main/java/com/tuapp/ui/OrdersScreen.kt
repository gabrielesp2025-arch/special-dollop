package com.tuapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "orders"
    ) {
        // Pantalla de lista de órdenes
        composable("orders") {
            OrdersScreen(
                onOrderClick = { orderId ->
                    navController.navigate("orderDetail/$orderId")
                }
            )
        }

        // Pantalla de detalle de orden
        composable(
            route = "orderDetail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderDetailScreen(orderId ?: "")
        }

        // Pantalla de referencia de precios
        composable("priceReference") {
            PriceReferenceScreen()
        }
    }
}
