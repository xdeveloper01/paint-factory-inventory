package com.paintfactory.inventory.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintfactory.inventory.ui.viewmodel.ReceiveLotViewModel
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveLotScreen(
    viewModel: ReceiveLotViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val material by viewModel.material.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var lotNumber by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("") }
    var manufacturingDateInput by rememberSaveable { mutableStateOf("") }
    var expiryDateInput by rememberSaveable { mutableStateOf("") }
    var receivedBy by rememberSaveable { mutableStateOf("") }
    var poNumber by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    var selectedLocationId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSupplierId by rememberSaveable { mutableStateOf<String?>(null) }

    var locationExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(locations) {
        if (selectedLocationId == null && locations.isNotEmpty()) {
            selectedLocationId = locations.first().id
        }
    }

    LaunchedEffect(suppliers) {
        if (selectedSupplierId == null && suppliers.isNotEmpty()) {
            selectedSupplierId = suppliers.first().id
        }
    }

    LaunchedEffect(isSaved) {
        if (isSaved) {
            viewModel.onSaveHandled()
            onNavigateBack()
        }
    }

    val selectedLocationName = locations.firstOrNull { it.id == selectedLocationId }?.name.orEmpty()
    val selectedSupplierName = suppliers.firstOrNull { it.id == selectedSupplierId }?.name.orEmpty()

    val quantity = quantityInput.toFloatOrNull()
    val expiryDate = parseDateToMillis(expiryDateInput)
    val manufacturingDate = parseDateToMillis(manufacturingDateInput)

    val canSubmit = lotNumber.isNotBlank() &&
        quantity != null && quantity > 0f &&
        expiryDate != null &&
        selectedLocationId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receive Lot") },
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
            Text(
                text = material?.nameEn ?: "Material not found",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = lotNumber,
                onValueChange = { lotNumber = it },
                label = { Text("Lot Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantityInput,
                onValueChange = { quantityInput = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                isError = quantityInput.isNotBlank() && quantity == null
            )

            OutlinedTextField(
                value = manufacturingDateInput,
                onValueChange = { manufacturingDateInput = it },
                label = { Text("Manufacturing Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                isError = manufacturingDateInput.isNotBlank() && manufacturingDate == null
            )

            OutlinedTextField(
                value = expiryDateInput,
                onValueChange = { expiryDateInput = it },
                label = { Text("Expiry Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                isError = expiryDateInput.isNotBlank() && expiryDate == null
            )

            OutlinedTextField(
                value = selectedLocationName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Storage Location") },
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { locationExpanded = true }
            )
            DropdownMenu(expanded = locationExpanded, onDismissRequest = { locationExpanded = false }) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location.name) },
                        onClick = {
                            selectedLocationId = location.id
                            locationExpanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = selectedSupplierName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Supplier") },
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { supplierExpanded = true }
            )
            DropdownMenu(expanded = supplierExpanded, onDismissRequest = { supplierExpanded = false }) {
                suppliers.forEach { supplier ->
                    DropdownMenuItem(
                        text = { Text(supplier.name) },
                        onClick = {
                            selectedSupplierId = supplier.id
                            supplierExpanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = receivedBy,
                onValueChange = { receivedBy = it },
                label = { Text("Received By") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = poNumber,
                onValueChange = { poNumber = it },
                label = { Text("PO Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (canSubmit) {
                        viewModel.receiveLot(
                            lotNumber = lotNumber,
                            quantity = quantity!!,
                            manufacturingDate = manufacturingDate,
                            expiryDate = expiryDate!!,
                            locationId = selectedLocationId,
                            supplierId = selectedSupplierId,
                            receivedBy = receivedBy,
                            poNumber = poNumber,
                            notes = notes
                        )
                    }
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Lot")
            }
        }
    }
}

private fun parseDateToMillis(value: String): Long? {
    if (value.isBlank()) return null
    return try {
        LocalDate.parse(value)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (_: Exception) {
        null
    }
}
