package com.paintfactory.inventory.ui.screens

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintfactory.inventory.data.local.entities.RawMaterial
import com.paintfactory.inventory.domain.util.ExportUtil
import com.paintfactory.inventory.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onAddMaterial: () -> Unit = {},
    onMaterialClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportUtil = remember(context) { ExportUtil(context) }
    val materials by viewModel.materials.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isRtl) "\u0627\u0644\u0645\u062e\u0632\u0648\u0646" else "Inventory",
                        textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val (allMaterials, allLots) = viewModel.getExportData()
                                val file = exportUtil.exportInventoryToCsv(allMaterials, allLots)
                                exportUtil.shareFile(file)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
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
