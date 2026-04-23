package com.paintfactory.inventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.paintfactory.inventory.data.local.dao.BatchDao
import com.paintfactory.inventory.data.local.dao.FormulaDao
import com.paintfactory.inventory.data.local.dao.InventoryDao
import com.paintfactory.inventory.data.local.dao.InventoryMovementDao
import com.paintfactory.inventory.data.local.dao.LocationDao
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.dao.SupplierDao
import com.paintfactory.inventory.data.local.dao.SyncDao
import com.paintfactory.inventory.data.local.entities.BatchStatus
import com.paintfactory.inventory.data.local.entities.ChemicalCategory
import com.paintfactory.inventory.data.local.entities.Formula
import com.paintfactory.inventory.data.local.entities.FormulaComponent
import com.paintfactory.inventory.data.local.entities.HazmatClass
import com.paintfactory.inventory.data.local.entities.InventoryLot
import com.paintfactory.inventory.data.local.entities.InventoryMovement
import com.paintfactory.inventory.data.local.entities.LocationType
import com.paintfactory.inventory.data.local.entities.LotConsumption
import com.paintfactory.inventory.data.local.entities.LotStatus
import com.paintfactory.inventory.data.local.entities.MeasureUnit
import com.paintfactory.inventory.data.local.entities.MovementType
import com.paintfactory.inventory.data.local.entities.OperationType
import com.paintfactory.inventory.data.local.entities.PaintType
import com.paintfactory.inventory.data.local.entities.PendingTransaction
import com.paintfactory.inventory.data.local.entities.ProductionBatch
import com.paintfactory.inventory.data.local.entities.QCStatus
import com.paintfactory.inventory.data.local.entities.RawMaterial
import com.paintfactory.inventory.data.local.entities.StorageLocation
import com.paintfactory.inventory.data.local.entities.Supplier
import com.paintfactory.inventory.data.local.entities.SyncStatus
import java.math.BigDecimal
import java.util.Date

@Database(
    entities = [
        RawMaterial::class,
        StorageLocation::class,
        Supplier::class,
        InventoryLot::class,
        InventoryMovement::class,
        Formula::class,
        FormulaComponent::class,
        ProductionBatch::class,
        LotConsumption::class,
        PendingTransaction::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao
    abstract fun locationDao(): LocationDao
    abstract fun supplierDao(): SupplierDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun inventoryMovementDao(): InventoryMovementDao
    abstract fun formulaDao(): FormulaDao
    abstract fun batchDao(): BatchDao
    abstract fun syncDao(): SyncDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.toBigDecimalOrNull()

    @TypeConverter
    fun fromCategory(value: ChemicalCategory) = value.name

    @TypeConverter
    fun toCategory(value: String) = enumValueOf<ChemicalCategory>(value)

    @TypeConverter
    fun fromMeasureUnit(value: MeasureUnit) = value.name

    @TypeConverter
    fun toMeasureUnit(value: String) = enumValueOf<MeasureUnit>(value)

    @TypeConverter
    fun fromHazmatClass(value: HazmatClass?) = value?.name

    @TypeConverter
    fun toHazmatClass(value: String?) = value?.let { enumValueOf<HazmatClass>(it) }

    @TypeConverter
    fun fromLocationType(value: LocationType) = value.name

    @TypeConverter
    fun toLocationType(value: String) = enumValueOf<LocationType>(value)

    @TypeConverter
    fun fromMovementType(value: MovementType) = value.name

    @TypeConverter
    fun toMovementType(value: String) = enumValueOf<MovementType>(value)

    @TypeConverter
    fun fromLotStatus(value: LotStatus) = value.name

    @TypeConverter
    fun toLotStatus(value: String) = enumValueOf<LotStatus>(value)

    @TypeConverter
    fun fromQCStatus(value: QCStatus) = value.name

    @TypeConverter
    fun toQCStatus(value: String) = enumValueOf<QCStatus>(value)

    @TypeConverter
    fun fromPaintType(value: PaintType) = value.name

    @TypeConverter
    fun toPaintType(value: String) = enumValueOf<PaintType>(value)

    @TypeConverter
    fun fromBatchStatus(value: BatchStatus) = value.name

    @TypeConverter
    fun toBatchStatus(value: String) = enumValueOf<BatchStatus>(value)

    @TypeConverter
    fun fromOperationType(value: OperationType) = value.name

    @TypeConverter
    fun toOperationType(value: String) = enumValueOf<OperationType>(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus) = value.name

    @TypeConverter
    fun toSyncStatus(value: String) = enumValueOf<SyncStatus>(value)
}
