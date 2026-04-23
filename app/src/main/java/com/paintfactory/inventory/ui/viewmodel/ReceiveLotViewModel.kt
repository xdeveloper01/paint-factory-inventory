package com.paintfactory.inventory.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintfactory.inventory.data.local.dao.InventoryDao
import com.paintfactory.inventory.data.local.dao.InventoryMovementDao
import com.paintfactory.inventory.data.local.dao.LocationDao
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.dao.SupplierDao
import com.paintfactory.inventory.data.local.entities.InventoryLot
import com.paintfactory.inventory.data.local.entities.InventoryMovement
import com.paintfactory.inventory.data.local.entities.LocationType
import com.paintfactory.inventory.data.local.entities.LotStatus
import com.paintfactory.inventory.data.local.entities.MovementType
import com.paintfactory.inventory.data.local.entities.QCStatus
import com.paintfactory.inventory.data.local.entities.RawMaterial
import com.paintfactory.inventory.data.local.entities.StorageLocation
import com.paintfactory.inventory.data.local.entities.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiveLotViewModel @Inject constructor(
    private val materialDao: MaterialDao,
    private val inventoryDao: InventoryDao,
    private val movementDao: InventoryMovementDao,
    private val locationDao: LocationDao,
    private val supplierDao: SupplierDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val materialId: String = savedStateHandle.get<String>("materialId").orEmpty()

    private val _material = MutableStateFlow<RawMaterial?>(null)
    val material: StateFlow<RawMaterial?> = _material.asStateFlow()

    val locations: StateFlow<List<StorageLocation>> = locationDao.getAllActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<Supplier>> = supplierDao.getAllActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        seedMasterDataIfNeeded()
        loadMaterial()
    }

    fun onSaveHandled() {
        _isSaved.value = false
    }

    fun onErrorShown() {
        _errorMessage.value = null
    }

    fun receiveLot(
        lotNumber: String,
        quantity: Float,
        manufacturingDate: Long?,
        expiryDate: Long,
        locationId: String?,
        supplierId: String?,
        receivedBy: String,
        poNumber: String?,
        notes: String?
    ) {
        val current = _material.value
        if (current == null) {
            _errorMessage.value = "Material not found"
            return
        }

        if (lotNumber.isBlank() || quantity <= 0f) {
            _errorMessage.value = "Lot number and quantity are required"
            return
        }

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val normalizedReceivedBy = receivedBy.ifBlank { "system" }
                val normalizedNotes = notes?.trim().orEmpty().ifBlank { null }
                val normalizedPoNumber = poNumber?.trim().orEmpty().ifBlank { null }

                val lot = InventoryLot(
                    lotNumber = lotNumber.trim(),
                    materialId = current.id,
                    locationId = locationId,
                    quantityCurrent = quantity,
                    quantityOriginal = quantity,
                    status = LotStatus.AVAILABLE,
                    manufacturingDate = manufacturingDate,
                    receivedDate = now,
                    openedDate = null,
                    expiryDate = expiryDate,
                    qcStatus = QCStatus.PENDING,
                    qcCertificatePath = null,
                    containerType = null,
                    containerId = null,
                    supplierId = supplierId,
                    poNumber = normalizedPoNumber,
                    receivedBy = normalizedReceivedBy,
                    notes = normalizedNotes,
                    deviceId = "LOCAL"
                )

                inventoryDao.insertLot(lot)

                val movement = InventoryMovement(
                    movementType = MovementType.RECEIPT,
                    materialId = current.id,
                    lotId = lot.id,
                    quantity = quantity,
                    unit = current.defaultUnit,
                    fromLocationId = null,
                    toLocationId = locationId,
                    referenceType = "RECEIVE_LOT",
                    referenceId = lot.id,
                    notes = normalizedNotes,
                    performedBy = normalizedReceivedBy,
                    createdAt = now
                )

                movementDao.insertMovement(movement)
                _isSaved.value = true
            } catch (_: Exception) {
                _errorMessage.value = "Could not save lot. Please try again."
            }
        }
    }

    private fun loadMaterial() {
        if (materialId.isBlank()) return

        viewModelScope.launch {
            _material.value = materialDao.getById(materialId)
        }
    }

    private fun seedMasterDataIfNeeded() {
        viewModelScope.launch {
            if (locationDao.countActive() == 0) {
                locationDao.insertAll(
                    listOf(
                        StorageLocation(code = "WH-RAW", name = "Raw Material Warehouse", type = LocationType.WAREHOUSE),
                        StorageLocation(code = "QC-HOLD", name = "QC Hold", type = LocationType.QC),
                        StorageLocation(code = "PROD-1", name = "Production Line 1", type = LocationType.PRODUCTION)
                    )
                )
            }

            if (supplierDao.countActive() == 0) {
                supplierDao.insertAll(
                    listOf(
                        Supplier(code = "SUP-001", name = "Local Chemical Traders"),
                        Supplier(code = "SUP-002", name = "Industrial Solvents Co."),
                        Supplier(code = "SUP-003", name = "Pigment Imports Ltd.")
                    )
                )
            }
        }
    }
}
