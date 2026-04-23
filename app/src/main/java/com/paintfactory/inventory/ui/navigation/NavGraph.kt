package com.paintfactory.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paintfactory.inventory.ui.screens.AddMaterialScreen
import com.paintfactory.inventory.ui.screens.InventoryScreen
import com.paintfactory.inventory.ui.screens.MaterialDetailScreen
import com.paintfactory.inventory.ui.screens.ReceiveLotScreen

private const val ROUTE_INVENTORY = "inventory"
private const val ROUTE_ADD_MATERIAL = "add_material"
private const val ROUTE_MATERIAL_DETAIL = "material_detail"
private const val ROUTE_RECEIVE_LOT = "receive_lot"
private const val ARG_MATERIAL_ID = "materialId"

@Composable
fun InventoryNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ROUTE_INVENTORY) {
        composable(ROUTE_INVENTORY) {
            InventoryScreen(
                onAddMaterial = { navController.navigate(ROUTE_ADD_MATERIAL) },
                onMaterialClick = { materialId ->
                    navController.navigate("$ROUTE_MATERIAL_DETAIL/$materialId")
                }
            )
        }

        composable(ROUTE_ADD_MATERIAL) {
            AddMaterialScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("$ROUTE_MATERIAL_DETAIL/{$ARG_MATERIAL_ID}") {
            MaterialDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onReceiveLot = { materialId ->
                    navController.navigate("$ROUTE_RECEIVE_LOT/$materialId")
                }
            )
        }

        composable("$ROUTE_RECEIVE_LOT/{$ARG_MATERIAL_ID}") {
            ReceiveLotScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
