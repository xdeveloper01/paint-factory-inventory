package com.paintfactory.inventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.entities.ChemicalCategory
import com.paintfactory.inventory.data.local.entities.MeasureUnit
import com.paintfactory.inventory.data.local.entities.RawMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddMaterialViewModel @Inject constructor(
    private val materialDao: MaterialDao
) : ViewModel() {
    
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
