package com.paintfactory.inventory.di

import android.content.Context
import androidx.room.Room
import com.paintfactory.inventory.data.local.AppDatabase
import com.paintfactory.inventory.data.local.dao.BatchDao
import com.paintfactory.inventory.data.local.dao.FormulaDao
import com.paintfactory.inventory.data.local.dao.InventoryDao
import com.paintfactory.inventory.data.local.dao.InventoryMovementDao
import com.paintfactory.inventory.data.local.dao.LocationDao
import com.paintfactory.inventory.data.local.dao.MaterialDao
import com.paintfactory.inventory.data.local.dao.SupplierDao
import com.paintfactory.inventory.data.local.dao.SyncDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "paint_inventory.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMaterialDao(database: AppDatabase): MaterialDao = database.materialDao()

    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao = database.locationDao()

    @Provides
    fun provideSupplierDao(database: AppDatabase): SupplierDao = database.supplierDao()

    @Provides
    fun provideInventoryDao(database: AppDatabase): InventoryDao = database.inventoryDao()

    @Provides
    fun provideInventoryMovementDao(database: AppDatabase): InventoryMovementDao =
        database.inventoryMovementDao()

    @Provides
    fun provideFormulaDao(database: AppDatabase): FormulaDao = database.formulaDao()

    @Provides
    fun provideBatchDao(database: AppDatabase): BatchDao = database.batchDao()

    @Provides
    fun provideSyncDao(database: AppDatabase): SyncDao = database.syncDao()
}
