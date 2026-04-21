package com.paintfactory.inventory.data.local.entities

import androidx.room.*
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "raw_materials", indices = [Index("sku", unique = true)])
data class RawMaterial(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
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
    val specificGravity: Float?,
    val isHazardous: Boolean,
    val casNumber: String?,
    val unNumber: String?,
    val hazardClass: HazmatClass?,
    val storageTempMin: Float?,
    val storageTempMax: Float?,
    val shelfLifeDays: Int,
    val reorderPoint: Float,
    val reorderQty: Float,
    val maxStockLevel: Float?,
    val standardCost: BigDecimal?,
    val currency: String = "USD",
    val preferredSupplierId: String?,
    val sdsDocumentEnPath: String?,
    val sdsDocumentArPath: String?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "inventory_lots",
    foreignKeys = [ForeignKey(entity = RawMaterial::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("materialId"), Index("lotNumber", unique = true), Index("expiryDate"), Index("status")]
)
data class InventoryLot(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val lotNumber: String,
    val materialId: String,
    val locationId: String?,
    val quantityCurrent: Float,
    val quantityOriginal: Float,
    val quantityReserved: Float = 0f,
    val status: LotStatus,
    val manufacturingDate: Long?,
    val receivedDate: Long,
    val openedDate: Long?,
    val expiryDate: Long,
    val qcStatus: QCStatus,
    val qcCertificatePath: String?,
    val containerType: String?,
    val containerId: String?,
    val supplierId: String?,
    val poNumber: String?,
    val receivedBy: String,
    val notes: String?,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis(),
    val deviceId: String = ""
)

@Entity(tableName = "formulas")
data class Formula(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val code: String,
    val nameEn: String,
    val nameAr: String?,
    val version: Int = 1,
    val isActive: Boolean = true,
    val previousVersionId: String?,
    val paintType: PaintType,
    val finishEn: String?,
    val finishAr: String?,
    val substrate: String?,
    val standardBatchSize: Float,
    val standardBatchUnit: MeasureUnit,
    val mixingTimeMinutes: Int?,
    val mixingSpeed: String?,
    val targetViscosity: Float?,
    val viscosityTolerance: Float?,
    val targetPH: Float?,
    val phTolerance: Float?,
    val estimatedRawCost: BigDecimal?,
    val mixingInstructionsEn: String,
    val mixingInstructionsAr: String?,
    val safetyNotesEn: String?,
    val safetyNotesAr: String?
)

@Entity(
    tableName = "formula_components",
    primaryKeys = ["formulaId", "materialId"],
    foreignKeys = [
        ForeignKey(entity = Formula::class, parentColumns = ["id"], childColumns = ["formulaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = RawMaterial::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FormulaComponent(
    val formulaId: String,
    val materialId: String,
    val sequence: Int,
    val percentage: Float,
    val tolerancePercent: Float,
    val isOptional: Boolean = false,
    val substitutionGroup: String?,
    val standardCostAtCreation: BigDecimal?
)

@Entity(
    tableName = "production_batches",
    foreignKeys = [ForeignKey(entity = Formula::class, parentColumns = ["id"], childColumns = ["formulaId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("batchNumber", unique = true), Index("status"), Index("plannedDate")]
)
data class ProductionBatch(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val batchNumber: String,
    val formulaId: String,
    val formulaVersion: Int,
    val plannedQty: Float,
    val actualQty: Float?,
    val unit: MeasureUnit,
    val yieldPercent: Float?,
    val status: BatchStatus,
    val plannedDate: Long,
    val startedDate: Long?,
    val completedDate: Long?,
    val mixerOperatorId: String?,
    val qcInspectorId: String?,
    val viscosityActual: Float?,
    val phActual: Float?,
    val colorDifference: Float?,
    val qcPassed: Boolean?,
    val qcNotes: String?,
    val actualCost: BigDecimal?,
    val parentBatchId: String?
)

@Entity(
    tableName = "lot_consumptions",
    foreignKeys = [
        ForeignKey(entity = ProductionBatch::class, parentColumns = ["id"], childColumns = ["batchId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = InventoryLot::class, parentColumns = ["id"], childColumns = ["lotId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("batchId"), Index("lotId")]
)
data class LotConsumption(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val batchId: String,
    val lotId: String,
    val materialId: String,
    val quantityRequired: Float,
    val quantityActual: Float,
    val unit: MeasureUnit,
    val variancePercent: Float?,
    val weighingSessionId: String?,
    val weighedBy: String,
    val weighedAt: Long,
    val remainingBefore: Float,
    val remainingAfter: Float
)

@Entity(
    tableName = "pending_transactions",
    indices = [Index("syncStatus"), Index("createdAt")]
)
data class PendingTransaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val operationType: OperationType,
    val entityType: String,
    val localEntityId: String,
    val payloadJson: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val attemptCount: Int = 0,
    val lastAttempt: Long? = null,
    val errorMessage: String? = null,
    val serverConfirmedAt: Long? = null,
    val idempotencyKey: String,
    val createdAt: Long = System.currentTimeMillis(),
    val sequenceNumber: Long
)

// Enums
enum class ChemicalCategory { PIGMENT, RESIN, SOLVENT, ADDITIVE, FILLER, THINNER, CATALYST }
enum class MeasureUnit { KILOGRAM, LITER, GRAM, MILLILITER, PIECE, GALLON, POUND }
enum class HazmatClass { FLAMMABLE_LIQUID, TOXIC, CORROSIVE, OXIDIZER, COMBUSTIBLE, NONE }
enum class LotStatus { QUARANTINE, AVAILABLE, RESERVED, BLOCKED, EXPIRED, EMPTY, DISPOSED }
enum class QCStatus { PENDING, PASSED, FAILED, CONDITIONAL }
enum class PaintType { PRIMER, UNDERCOAT, BASE, FINISH, TEXTURE, INDUSTRIAL, SPECIALTY }
enum class BatchStatus { PLANNED, ISSUED, MIXING, QC_TESTING, FINISHED, REJECTED, CANCELLED }
enum class OperationType { LOT_CONSUMPTION, INVENTORY_RECEIPT, INVENTORY_ADJUSTMENT, LOT_MOVE, BATCH_CREATION, QC_UPDATE }
enum class SyncStatus { PENDING, IN_PROGRESS, FAILED_RETRYABLE, FAILED_FATAL, SYNCED }
