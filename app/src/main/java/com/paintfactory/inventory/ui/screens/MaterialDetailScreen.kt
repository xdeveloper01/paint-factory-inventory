package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintfactory.inventory.ui.viewmodel.MaterialDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailScreen(
    viewModel: MaterialDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val material by viewModel.material.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (material == null) {
                Text("Material not found", style = MaterialTheme.typography.bodyLarge)
            } else {
                val current = material!!
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(current.nameEn, style = MaterialTheme.typography.headlineSmall)
                        Text("SKU: ${current.sku}")
                        Text("Category: ${current.category}")
                        Text("Unit: ${current.defaultUnit}")
                        Text("Reorder Point: ${current.reorderPoint}")
                        Text("Reorder Qty: ${current.reorderQty}")
                        Text("Hazardous: ${if (current.isHazardous) "Yes" else "No"}")
                    }
                }
            }
        }
    }
}
