package com.paintfactory.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paintfactory.inventory.ui.screens.*

@Composable
fun InventoryNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "inventory") {
        composable("inventory") {
            InventoryScreen(
                onAddMaterial = { navController.navigate("add_material") },
                onMaterialClick = { materialId ->
                    // Navigate to detail
                }
            )
        }
        composable("add_material") {
            AddMaterialScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
