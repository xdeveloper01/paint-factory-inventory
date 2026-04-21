package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    val isDeleted by viewModel.isDeleted.collectAsState()

    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var nameEn by rememberSaveable { mutableStateOf("") }
    var nameAr by rememberSaveable { mutableStateOf("") }
    var reorderPointInput by rememberSaveable { mutableStateOf("") }
    var reorderQtyInput by rememberSaveable { mutableStateOf("") }
    var isHazardous by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(material?.id) {
        val current = material ?: return@LaunchedEffect
        if (!isEditMode) {
            nameEn = current.nameEn
            nameAr = current.nameAr.orEmpty()
            reorderPointInput = current.reorderPoint.toString()
            reorderQtyInput = current.reorderQty.toString()
            isHazardous = current.isHazardous
        }
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }

    val reorderPoint = reorderPointInput.toFloatOrNull()
    val reorderQty = reorderQtyInput.toFloatOrNull()
    val canSave = nameEn.isNotBlank() && reorderPoint != null && reorderQty != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditMode) {
                        TextButton(
                            onClick = {
                                isEditMode = false
                                material?.let { current ->
                                    nameEn = current.nameEn
                                    nameAr = current.nameAr.orEmpty()
                                    reorderPointInput = current.reorderPoint.toString()
                                    reorderQtyInput = current.reorderQty.toString()
                                    isHazardous = current.isHazardous
                                }
                            }
                        ) {
                            Text("Cancel")
                        }

                        TextButton(
                            enabled = canSave,
                            onClick = {
                                viewModel.updateMaterial(
                                    nameEn = nameEn,
                                    nameAr = nameAr,
                                    reorderPoint = reorderPoint!!,
                                    reorderQty = reorderQty!!,
                                    isHazardous = isHazardous
                                )
                                isEditMode = false
                            }
                        ) {
                            Text("Save")
                        }
                    } else {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
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
                        if (isEditMode) {
                            OutlinedTextField(
                                value = nameEn,
                                onValueChange = { nameEn = it },
                                label = { Text("Name (English)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = nameAr,
                                onValueChange = { nameAr = it },
                                label = { Text("Name (Arabic)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = reorderPointInput,
                                onValueChange = { reorderPointInput = it },
                                label = { Text("Reorder Point") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = reorderPointInput.isNotBlank() && reorderPoint == null
                            )

                            OutlinedTextField(
                                value = reorderQtyInput,
                                onValueChange = { reorderQtyInput = it },
                                label = { Text("Reorder Quantity") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = reorderQtyInput.isNotBlank() && reorderQty == null
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Hazardous")
                                Switch(
                                    checked = isHazardous,
                                    onCheckedChange = { isHazardous = it }
                                )
                            }
                        } else {
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Material") },
                text = { Text("Are you sure you want to delete this material?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteMaterial()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
