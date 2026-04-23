package com.paintfactory.inventory.data.local.entities

import androidx.room.*
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "raw_materials", indices = [Index("sku", unique = true)])
data class RawMaterial(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sku: String,
    val nameEn: String,
    val nameAr: String? = null,
    val shortNameEn: String,
    val shortNameAr: String? = null,
    val descriptionEn: String? = null,
    val descriptionAr: String? = null,
    val category: ChemicalCategory,
    val subCategory: String? = null,
    val defaultUnit: MeasureUnit,
    val density: Float? = null,
    val specificGravity: Float? = null,
    val isHazardous: Boolean,
    val casNumber: String? = null,
    val unNumber: String? = null,
    val hazardClass: HazmatClass? = null,
    val storageTempMin: Float? = null,
    val storageTempMax: Float? = null,
    val shelfLifeDays: Int,
    val reorderPoint: Float,
    val reorderQty: Float,
    val maxStockLevel: Float? = null,
    val standardCost: BigDecimal? = null,
    val currency: String = "USD",
    val preferredSupplierId: String? = null,
    val sdsDocumentEnPath: String? = null,
    val sdsDocumentArPath: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "storage_locations",
    indices = [Index("code", unique = true), Index("isActive")]
)
data class StorageLocation(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val code: String,
    val name: String,
    val type: LocationType,
    val parentLocationId: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "suppliers",
    indices = [Index("code", unique = true), Index("isActive")]
)
data class Supplier(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val code: String,
    val name: String,
    val contactName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
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

@Entity(
    tableName = "inventory_movements",
    foreignKeys = [
        ForeignKey(entity = RawMaterial::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = InventoryLot::class, parentColumns = ["id"], childColumns = ["lotId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = StorageLocation::class, parentColumns = ["id"], childColumns = ["fromLocationId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = StorageLocation::class, parentColumns = ["id"], childColumns = ["toLocationId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [
        Index("materialId"),
        Index("lotId"),
        Index("movementType"),
        Index("fromLocationId"),
        Index("toLocationId"),
        Index("createdAt")
    ]
)
data class InventoryMovement(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val movementType: MovementType,
    val materialId: String,
    val lotId: String? = null,
    val quantity: Float,
    val unit: MeasureUnit,
    val fromLocationId: String? = null,
    val toLocationId: String? = null,
    val referenceType: String? = null,
    val referenceId: String? = null,
    val notes: String? = null,
    val performedBy: String,
    val createdAt: Long = System.currentTimeMillis()
)
@Entity(tableName = "formulas")
data class Formula(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val code: String,
    val nameEn: String,
    val nameAr: String? = null,
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
    indices = [Index("formulaId"), Index("materialId")],
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
    indices = [Index("batchNumber", unique = true), Index("status"), Index("plannedDate"), Index("formulaId")]
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
enum class LocationType { WAREHOUSE, PRODUCTION, QC, QUARANTINE }
enum class MovementType { RECEIPT, ADJUSTMENT, TRANSFER, RESERVATION, RELEASE, CONSUMPTION }
enum class LotStatus { QUARANTINE, AVAILABLE, RESERVED, BLOCKED, EXPIRED, EMPTY, DISPOSED }
enum class QCStatus { PENDING, PASSED, FAILED, CONDITIONAL }
enum class PaintType { PRIMER, UNDERCOAT, BASE, FINISH, TEXTURE, INDUSTRIAL, SPECIALTY }
enum class BatchStatus { PLANNED, ISSUED, MIXING, QC_TESTING, FINISHED, REJECTED, CANCELLED }
enum class OperationType { LOT_CONSUMPTION, INVENTORY_RECEIPT, INVENTORY_ADJUSTMENT, LOT_MOVE, BATCH_CREATION, QC_UPDATE }
enum class SyncStatus { PENDING, IN_PROGRESS, FAILED_RETRYABLE, FAILED_FATAL, SYNCED }
object SampleData {
    fun getSampleMaterials(): List<RawMaterial> = listOf(
        RawMaterial(
            sku = "PIG-TIO2-001",
            nameEn = "Titanium Dioxide Rutile",
            nameAr = "ثاني أكسيد التيتانيوم الروتيلي",
            shortNameEn = "TiO2-Rutile",
            shortNameAr = "TiO2-روتيل",
            category = ChemicalCategory.PIGMENT,
            defaultUnit = MeasureUnit.KILOGRAM,
            density = 4.23f,
            isHazardous = false,
            shelfLifeDays = 730,
            reorderPoint = 500f,
            reorderQty = 1000f,
            standardCost = BigDecimal("15.50")
        ),
        RawMaterial(
            sku = "RES-EPOX-001", 
            nameEn = "Epoxy Resin",
            nameAr = "راتنج إيبوكسي",
            shortNameEn = "Epoxy-Resin",
            shortNameAr = "إيبوكسي",
            category = ChemicalCategory.RESIN,
            defaultUnit = MeasureUnit.KILOGRAM,
            density = 1.16f,
            isHazardous = true,
            hazardClass = HazmatClass.TOXIC,
            shelfLifeDays = 365,
            reorderPoint = 200f,
            reorderQty = 500f,
            standardCost = BigDecimal("8.75")
        ),
        RawMaterial(
            sku = "SOLV-TOLU-001",
            nameEn = "Toluene Solvent",
            nameAr = "مذيب التولوين",
            shortNameEn = "Toluene",
            shortNameAr = "تولوين",
            category = ChemicalCategory.SOLVENT,
            defaultUnit = MeasureUnit.LITER,
            density = 0.87f,
            isHazardous = true,
            hazardClass = HazmatClass.FLAMMABLE_LIQUID,
            shelfLifeDays = 1095,
            reorderPoint = 300f,
            reorderQty = 1000f,
            standardCost = BigDecimal("3.25")
        ),
        RawMaterial(
            sku = "ADD-DEFO-001",
            nameEn = "Defoamer",
            nameAr = "مضاد الرغوة",
            shortNameEn = "Defoamer",
            shortNameAr = "مضاد رغوة",
            category = ChemicalCategory.ADDITIVE,
            defaultUnit = MeasureUnit.KILOGRAM,
            density = 0.95f,
            isHazardous = false,
            shelfLifeDays = 540,
            reorderPoint = 50f,
            reorderQty = 100f,
            standardCost = BigDecimal("12.00")
        )
    )
}
