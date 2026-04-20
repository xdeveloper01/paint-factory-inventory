package com.paintfactory.inventory.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "raw_materials",
    indices = [Index("sku", unique = true)]
)
data class RawMaterial(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val sku: String,
    val nameEn: String,
    val nameAr: String?,
    val shortNameEn: String,
    val shortNameAr: String?,
    val descriptionEn: String?,
    val descriptionAr: String?,
    val category: ChemicalCategory,
    val subCategory: String?,
    val defaultUnit: MeasureUnit,
    val density: Float?,
    val isHazardous: Boolean,
    val shelfLifeDays: Int,
    val reorderPoint: Float,
    val standardCost: BigDecimal?,
    val isActive: Boolean = true
)

enum class ChemicalCategory { PIGMENT, RESIN, SOLVENT, ADDITIVE, FILLER }
enum class MeasureUnit { KILOGRAM, LITER, GRAM, MILLILITER, PIECE }
