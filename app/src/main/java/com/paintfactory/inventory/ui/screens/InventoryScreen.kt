package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintfactory.inventory.data.local.entities.RawMaterial
import com.paintfactory.inventory.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onAddMaterial: () -> Unit = {},
    onMaterialClick: (String) -> Unit = {}
) {
    val materials by viewModel.materials.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRtl = LocalContext.current.resources.configuration.layoutDirection == 
                android.view.View.LAYOUT_DIRECTION_RTL
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isRtl) "المخزون" else "Inventory",
                        textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    ) 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMaterial) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            
            LazyColumn {
                items(materials) { material ->
                    MaterialCard(material = material, onClick = { onMaterialClick(material.id) })
                }
            }
        }
    }
}

@Composable
fun MaterialCard(material: RawMaterial, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = material.nameEn,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "SKU: ${material.sku}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Category: ${material.category}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
