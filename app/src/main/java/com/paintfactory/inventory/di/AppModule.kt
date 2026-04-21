package com.paintfactory.inventory.di

import android.content.Context
import androidx.room.Room
import com.paintfactory.inventory.data.local.AppDatabase
import com.paintfactory.inventory.data.local.dao.*
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
    fun provideMaterialDao(database: AppDatabase): MaterialDao {
        return database.materialDao()
    }

    @Provides
    fun provideInventoryDao(database: AppDatabase): InventoryDao {
        return database.inventoryDao()
    }

    @Provides
    fun provideFormulaDao(database: AppDatabase): FormulaDao {
        return database.formulaDao()
    }

    @Provides
    fun provideBatchDao(database: AppDatabase): BatchDao {
        return database.batchDao()
    }

    @Provides
    fun provideSyncDao(database: AppDatabase): SyncDao {
        return database.syncDao()
    }
}
