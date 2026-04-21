package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.paintfactory.inventory.data.local.dao.InventoryDao
import kotlinx.coroutines.flow.first

@Composable
fun DashboardScreen(
    inventoryDao: InventoryDao,
    onNavigateToInventory: () -> Unit
) {
    var lowStockCount by remember { mutableIntStateOf(0) }
    var expiringCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val currentTime = System.currentTimeMillis()
        val thirtyDaysLater = currentTime + (30L * 24 * 60 * 60 * 1000)

        lowStockCount = inventoryDao.getLowStockMaterials().first().size
        expiringCount = inventoryDao.getExpiringLots(currentTime, thirtyDaysLater).first().size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (lowStockCount > 0 || expiringCount > 0) {
                    Color(0xFFFFF3E0)
                } else {
                    Color(0xFFE8F5E9)
                }
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("System Status", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (lowStockCount > 0) {
                    AlertRow(
                        icon = Icons.Default.Warning,
                        color = Color(0xFFFF6F00),
                        text = "$lowStockCount materials below reorder point"
                    )
                }

                if (expiringCount > 0) {
                    AlertRow(
                        icon = Icons.Default.Warning,
                        color = Color(0xFFD32F2F),
                        text = "$expiringCount lots expiring within 30 days"
                    )
                }

                if (lowStockCount == 0 && expiringCount == 0) {
                    Text("All stock levels normal", color = Color(0xFF2E7D32))
                }
            }
        }

        Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onNavigateToInventory, modifier = Modifier.weight(1f)) {
                Text("View Inventory")
            }
        }
    }
}

@Composable
fun AlertRow(icon: ImageVector, color: Color, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = color)
    }
}
