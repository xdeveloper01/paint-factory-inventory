package com.paintfactory.inventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.paintfactory.inventory.data.local.entities.*
import java.math.BigDecimal

@Database(
    entities = [RawMaterial::class, InventoryLot::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // DAOs will go here later
}

class Converters {
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
}
