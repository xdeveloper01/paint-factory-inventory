package com.paintfactory.inventory.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintfactory.inventory.data.local.dao.InventoryDao
import com.paintfactory.inventory.data.local.dao.InventoryMovementDao
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.entities.InventoryLot
import com.paintfactory.inventory.data.local.entities.InventoryMovement
import com.paintfactory.inventory.data.local.entities.LotStatus
import com.paintfactory.inventory.data.local.entities.RawMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialDetailViewModel @Inject constructor(
    private val materialDao: MaterialDao,
    private val inventoryDao: InventoryDao,
    private val inventoryMovementDao: InventoryMovementDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val materialId: String = savedStateHandle.get<String>("materialId").orEmpty()

    private val _material = MutableStateFlow<RawMaterial?>(null)
    val material: StateFlow<RawMaterial?> = _material.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    private val _lots = MutableStateFlow<List<InventoryLot>>(emptyList())
    val lots: StateFlow<List<InventoryLot>> = _lots.asStateFlow()

    private val _movements = MutableStateFlow<List<InventoryMovement>>(emptyList())
    val movements: StateFlow<List<InventoryMovement>> = _movements.asStateFlow()

    private val _stockOnHand = MutableStateFlow(0f)
    val stockOnHand: StateFlow<Float> = _stockOnHand.asStateFlow()

    init {
        loadMaterial()
        observeLots()
        observeMovements()
    }

    private fun loadMaterial() {
        if (materialId.isBlank()) return

        viewModelScope.launch {
            _material.value = materialDao.getById(materialId)
        }
    }

    private fun observeLots() {
        if (materialId.isBlank()) return

        viewModelScope.launch {
            inventoryDao.getLotsForMaterial(materialId).collectLatest { entries ->
                _lots.value = entries
                _stockOnHand.value = entries
                    .filter { it.status == LotStatus.AVAILABLE }
                    .sumOf { it.quantityCurrent.toDouble() }
                    .toFloat()
            }
        }
    }

    private fun observeMovements() {
        if (materialId.isBlank()) return

        viewModelScope.launch {
            inventoryMovementDao.getMovementsByMaterialId(materialId).collectLatest { entries ->
                _movements.value = entries
            }
        }
    }

    fun updateMaterial(
        nameEn: String,
        nameAr: String?,
        reorderPoint: Float,
        reorderQty: Float,
        isHazardous: Boolean
    ) {
        val current = _material.value ?: return

        val normalizedNameAr = nameAr?.trim().orEmpty().ifBlank { null }

        viewModelScope.launch {
            val updated = current.copy(
                nameEn = nameEn.trim(),
                nameAr = normalizedNameAr,
                shortNameEn = nameEn.trim().take(20),
                shortNameAr = normalizedNameAr?.take(20),
                reorderPoint = reorderPoint,
                reorderQty = reorderQty,
                isHazardous = isHazardous,
                updatedAt = System.currentTimeMillis()
            )

            materialDao.update(updated)
            _material.value = materialDao.getById(current.id)
        }
    }

    fun deleteMaterial() {
        if (materialId.isBlank()) return

        viewModelScope.launch {
            materialDao.softDeleteById(materialId, System.currentTimeMillis())
            _isDeleted.value = true
        }
    }
}
