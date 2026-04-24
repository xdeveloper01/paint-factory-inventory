package com.paintfactory.inventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintfactory.inventory.data.local.dao.InventoryDao
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.entities.ChemicalCategory
import com.paintfactory.inventory.data.local.entities.InventoryLot
import com.paintfactory.inventory.data.local.entities.MeasureUnit
import com.paintfactory.inventory.data.local.entities.RawMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModel @Inject constructor(
    private val materialDao: MaterialDao,
    private val inventoryDao: InventoryDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val materials: StateFlow<List<RawMaterial>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                materialDao.getAll()
            } else {
                flow { emit(materialDao.searchMaterials(query)) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    suspend fun getExportData(): Pair<List<RawMaterial>, List<InventoryLot>> {
        val allMaterials = materialDao.getAll().first()
        val allLots = inventoryDao.getAllLots().first()
        return allMaterials to allLots
    }

    fun addMaterial(
        sku: String,
        nameEn: String,
        nameAr: String?,
        category: ChemicalCategory,
        unit: MeasureUnit,
        density: Float?,
        isHazardous: Boolean
    ) {
        viewModelScope.launch {
            val material = RawMaterial(
                sku = sku,
                nameEn = nameEn,
                nameAr = nameAr,
                shortNameEn = nameEn.take(20),
                shortNameAr = nameAr?.take(20),
                descriptionEn = null,
                descriptionAr = null,
                category = category,
                subCategory = null,
                defaultUnit = unit,
                density = density,
                specificGravity = null,
                isHazardous = isHazardous,
                casNumber = null,
                unNumber = null,
                hazardClass = null,
                storageTempMin = null,
                storageTempMax = null,
                shelfLifeDays = 365,
                reorderPoint = 100f,
                reorderQty = 500f,
                maxStockLevel = null,
                standardCost = BigDecimal.ZERO,
                preferredSupplierId = null,
                sdsDocumentEnPath = null,
                sdsDocumentArPath = null
            )
            materialDao.insert(material)
        }
    }
}
