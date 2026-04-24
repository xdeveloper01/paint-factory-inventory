package com.paintfactory.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paintfactory.inventory.data.local.entities.BatchStatus
import com.paintfactory.inventory.data.local.entities.ChemicalCategory
import com.paintfactory.inventory.data.local.entities.Formula
import com.paintfactory.inventory.data.local.entities.FormulaComponent
import com.paintfactory.inventory.data.local.entities.InventoryLot
import com.paintfactory.inventory.data.local.entities.InventoryMovement
import com.paintfactory.inventory.data.local.entities.LotConsumption
import com.paintfactory.inventory.data.local.entities.PendingTransaction
import com.paintfactory.inventory.data.local.entities.ProductionBatch
import com.paintfactory.inventory.data.local.entities.RawMaterial
import com.paintfactory.inventory.data.local.entities.Supplier
import com.paintfactory.inventory.data.local.entities.SyncStatus
import com.paintfactory.inventory.data.local.entities.LotStatus
import com.paintfactory.inventory.data.local.entities.StorageLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM raw_materials WHERE isActive = 1 ORDER BY nameEn ASC")
    fun getAll(): Flow<List<RawMaterial>>

    @Query("SELECT * FROM raw_materials WHERE isActive = 1 ORDER BY nameAr ASC")
    fun getAllArabic(): Flow<List<RawMaterial>>

    @Query(
        """
        SELECT * FROM raw_materials
        WHERE (nameEn LIKE '%' || :query || '%' OR nameAr LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%')
        AND isActive = 1
        """
    )
    suspend fun searchMaterials(query: String): List<RawMaterial>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: RawMaterial)

    @Update
    suspend fun update(material: RawMaterial)

    @Query("SELECT * FROM raw_materials WHERE id = :id")
    suspend fun getById(id: String): RawMaterial?

    @Query("SELECT * FROM raw_materials WHERE category = :category AND isActive = 1")
    fun getByCategory(category: ChemicalCategory): Flow<List<RawMaterial>>

    @Query("UPDATE raw_materials SET isActive = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteById(id: String, timestamp: Long)
}

@Dao
interface InventoryDao {
    @Query(
        """
        SELECT * FROM inventory_lots
        WHERE materialId = :materialId
        AND status = 'AVAILABLE'
        AND expiryDate > :currentTime
        ORDER BY receivedDate ASC
        """
    )
    suspend fun getAvailableLotsOrderedByExpiry(materialId: String, currentTime: Long): List<InventoryLot>

    @Query(
        """
        SELECT materialId,
        CAST(SUM(quantityCurrent) AS REAL) as availableQty
        FROM inventory_lots
        WHERE status = 'AVAILABLE'
        GROUP BY materialId
        """
    )
    fun getStockAvailability(): Flow<List<StockAvailability>>

    @Query("UPDATE inventory_lots SET quantityCurrent = quantityCurrent - :amount WHERE id = :lotId")
    suspend fun consumeFromLot(lotId: String, amount: Float)

    @Insert
    suspend fun insertLot(lot: InventoryLot)

    @Update
    suspend fun updateLot(lot: InventoryLot)

    @Query("SELECT * FROM inventory_lots WHERE id = :lotId")
    suspend fun getLotById(lotId: String): InventoryLot?

    @Query("SELECT * FROM inventory_lots WHERE materialId = :materialId ORDER BY receivedDate DESC")
    fun getLotsForMaterial(materialId: String): Flow<List<InventoryLot>>

    @Query("SELECT * FROM inventory_lots WHERE status = :status ORDER BY receivedDate DESC")
    fun getAllLotsByStatus(status: LotStatus): Flow<List<InventoryLot>>

    @Query("SELECT * FROM inventory_lots WHERE locationId = :locationId ORDER BY receivedDate DESC")
    fun getLotsByLocation(locationId: String): Flow<List<InventoryLot>>

    @Query("SELECT * FROM inventory_lots ORDER BY receivedDate DESC")
    fun getAllLots(): Flow<List<InventoryLot>>

    @Query(
        """
        SELECT rm.* FROM raw_materials rm
        WHERE rm.isActive = 1
        AND rm.reorderPoint >= (
            SELECT COALESCE(SUM(quantityCurrent), 0)
            FROM inventory_lots
            WHERE materialId = rm.id AND status = 'AVAILABLE'
        )
        """
    )
    fun getLowStockMaterials(): Flow<List<RawMaterial>>

    @Query(
        """
        SELECT * FROM inventory_lots
        WHERE status = 'AVAILABLE'
        AND expiryDate BETWEEN :currentTime AND :warningTime
        ORDER BY expiryDate ASC
        """
    )
    fun getExpiringLots(currentTime: Long, warningTime: Long): Flow<List<InventoryLot>>

    @Query(
        """
        SELECT COUNT(*) FROM inventory_lots
        WHERE status = 'AVAILABLE'
        AND expiryDate < :currentTime
        """
    )
    suspend fun getExpiredCount(currentTime: Long): Int

    data class StockAvailability(
        val materialId: String,
        val availableQty: Float
    )
}

@Dao
interface InventoryMovementDao {
    @Insert
    suspend fun insertMovement(movement: InventoryMovement)

    @Query("SELECT * FROM inventory_movements WHERE lotId = :lotId ORDER BY createdAt DESC")
    fun getMovementsByLotId(lotId: String): Flow<List<InventoryMovement>>

    @Query("SELECT * FROM inventory_movements WHERE materialId = :materialId ORDER BY createdAt DESC")
    fun getMovementsByMaterialId(materialId: String): Flow<List<InventoryMovement>>
}

@Dao
interface LocationDao {
    @Query("SELECT * FROM storage_locations WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<StorageLocation>>

    @Query("SELECT * FROM storage_locations WHERE id = :id")
    suspend fun getById(id: String): StorageLocation?

    @Query("SELECT COUNT(*) FROM storage_locations WHERE isActive = 1")
    suspend fun countActive(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: StorageLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<StorageLocation>)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getById(id: String): Supplier?

    @Query("SELECT COUNT(*) FROM suppliers WHERE isActive = 1")
    suspend fun countActive(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suppliers: List<Supplier>)
}

@Dao
interface FormulaDao {
    @Query("SELECT * FROM formulas WHERE isActive = 1")
    fun getAllActive(): Flow<List<Formula>>

    @Query("SELECT * FROM formulas WHERE id = :formulaId")
    suspend fun getById(formulaId: String): Formula?

    @Query("SELECT * FROM formula_components WHERE formulaId = :formulaId ORDER BY sequence ASC")
    suspend fun getComponents(formulaId: String): List<FormulaComponent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormula(formula: Formula)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: FormulaComponent)
}

@Dao
interface BatchDao {
    @Insert
    suspend fun insertBatch(batch: ProductionBatch)

    @Insert
    suspend fun insertConsumption(consumption: LotConsumption)

    @Query("SELECT * FROM production_batches WHERE status = :status ORDER BY plannedDate DESC")
    fun getBatchesByStatus(status: BatchStatus): Flow<List<ProductionBatch>>

    @Query("SELECT * FROM production_batches WHERE id = :batchId")
    suspend fun getBatchById(batchId: String): ProductionBatch?

    @Query("SELECT * FROM lot_consumptions WHERE batchId = :batchId")
    suspend fun getConsumptionsForBatch(batchId: String): List<LotConsumption>

    @Update
    suspend fun updateBatch(batch: ProductionBatch)
}

@Dao
interface SyncDao {
    @Query("SELECT * FROM pending_transactions WHERE syncStatus = 'PENDING' ORDER BY sequenceNumber ASC")
    suspend fun getPendingOrderedBySequence(): List<PendingTransaction>

    @Insert
    suspend fun insertTransaction(transaction: PendingTransaction)

    @Query("UPDATE pending_transactions SET syncStatus = :status, attemptCount = attemptCount + 1, lastAttempt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("SELECT COUNT(*) FROM pending_transactions WHERE syncStatus = 'PENDING'")
    fun getPendingCount(): Flow<Int>
}
