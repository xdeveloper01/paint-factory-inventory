package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintfactory.inventory.data.local.entities.ChemicalCategory
import com.paintfactory.inventory.data.local.entities.MeasureUnit
import com.paintfactory.inventory.ui.viewmodel.AddMaterialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialScreen(
    viewModel: AddMaterialViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val isRtl = LocalContext.current.resources.configuration.layoutDirection == 
                android.view.View.LAYOUT_DIRECTION_RTL
    
    var sku by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var nameAr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ChemicalCategory.PIGMENT) }
    var selectedUnit by remember { mutableStateOf(MeasureUnit.KILOGRAM) }
    var density by remember { mutableStateOf("") }
    var isHazardous by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isRtl) "إضافة مادة" else "Add Material") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = sku,
                onValueChange = { sku = it.uppercase() },
                label = { Text("SKU") },
                modifier = Modifier.fillMaxWidth()
            )
            
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
            
            Button(
                onClick = {
                    viewModel.addMaterial(
                        sku = sku,
                        nameEn = nameEn,
                        nameAr = nameAr,
                        category = selectedCategory,
                        unit = selectedUnit,
                        density = density.toFloatOrNull(),
                        isHazardous = isHazardous
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
