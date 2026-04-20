package com.paintfactory.inventory.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_lots",
    foreignKeys = [
        ForeignKey(
            entity = RawMaterial::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("materialId"), Index("lotNumber", unique = true)]
)
data class InventoryLot(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val lotNumber: String,
    val materialId: String,
    val quantityCurrent: Float,
    val quantityOriginal: Float,
    val expiryDate: Long,
    val receivedDate: Long = System.currentTimeMillis(),
    val status: String = "AVAILABLE"
)
