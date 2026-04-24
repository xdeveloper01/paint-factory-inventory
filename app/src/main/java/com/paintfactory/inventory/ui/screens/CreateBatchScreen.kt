package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paintfactory.inventory.data.local.dao.FormulaDao
import com.paintfactory.inventory.data.local.dao.InventoryDao
import com.paintfactory.inventory.data.local.entities.Formula
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun CreateBatchScreen(
    formulaDao: FormulaDao,
    inventoryDao: InventoryDao,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedFormula by remember { mutableStateOf<Formula?>(null) }
    var targetQuantity by remember { mutableStateOf("1000") }
    var calculatedComponents by remember { mutableStateOf<List<CalculatedComponent>>(emptyList()) }

    val formulas by formulaDao.getAllActive().collectAsState(initial = emptyList())
    LaunchedEffect(formulas) {
        if (selectedFormula == null && formulas.isNotEmpty()) {
            selectedFormula = formulas.first()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Batch") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = selectedFormula?.nameEn ?: "No active formula",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = targetQuantity,
                onValueChange = { targetQuantity = it },
                label = { Text("Target Quantity (Liters)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    selectedFormula?.let { formula ->
                        scope.launch {
                            val components = formulaDao.getComponents(formula.id)
                            val target = targetQuantity.toFloatOrNull() ?: 0f
                            val scale = if (formula.standardBatchSize == 0f) 0f else target / formula.standardBatchSize

                            calculatedComponents = components.map { comp ->
                                CalculatedComponent(
                                    materialId = comp.materialId,
                                    materialName = comp.materialId,
                                    requiredQty = comp.percentage / 100f * formula.standardBatchSize * scale,
                                    unit = formula.standardBatchUnit.name,
                                    tolerance = comp.tolerancePercent
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Requirements")
            }

            LazyColumn {
                items(calculatedComponents) { comp ->
                    ComponentRow(component = comp)
                }
            }
        }
    }
}

data class CalculatedComponent(
    val materialId: String,
    val materialName: String,
    val requiredQty: Float,
    val unit: String,
    val tolerance: Float
)

@Composable
fun ComponentRow(component: CalculatedComponent) {
    val df = DecimalFormat("#.##")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(component.materialName, style = MaterialTheme.typography.titleSmall)
                Text("Required: ${df.format(component.requiredQty)} ${component.unit}")
            }
            Text("±${component.tolerance}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}
