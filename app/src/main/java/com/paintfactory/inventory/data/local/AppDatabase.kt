package com.paintfactory.inventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.paintfactory.inventory.data.local.dao.*
import com.paintfactory.inventory.data.local.entities.*
import java.math.BigDecimal
import java.util.*

@Database(
    entities = [
        RawMaterial::class,
        InventoryLot::class,
        Formula::class,
        FormulaComponent::class,
        ProductionBatch::class,
        LotConsumption::class,
        PendingTransaction::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao
    abstract fun inventoryDao(): InventoryDao
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
