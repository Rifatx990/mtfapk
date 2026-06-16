package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TailorDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<TailorOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: TailorOrder): Long

    @Delete
    suspend fun deleteOrder(order: TailorOrder)

    @Update
    suspend fun updateOrder(order: TailorOrder)

    @Query("SELECT * FROM inventory ORDER BY name ASC")
    fun getAllInventory(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(item: InventoryItem)

    @Delete
    suspend fun deleteInventory(item: InventoryItem)

    @Update
    suspend fun updateInventory(item: InventoryItem)

    @Query("SELECT * FROM measurements WHERE customerId = :customerId")
    fun getMeasurementsForCustomer(customerId: Int): Flow<List<Measurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: Measurement)

    @Query("SELECT * FROM workers ORDER BY name ASC")
    fun getAllWorkers(): Flow<List<Worker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: Worker): Long

    @Delete
    suspend fun deleteWorker(worker: Worker)

    @Update
    suspend fun updateWorker(worker: Worker)
}
