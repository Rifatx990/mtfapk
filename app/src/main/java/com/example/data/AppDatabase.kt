package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Customer::class, TailorOrder::class, InventoryItem::class, Measurement::class, Worker::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tailorDao(): TailorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mehedi_tailors_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(SeedDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.tailorDao()
                    
                    // Seed Customers
                    val customer1 = Customer(name = "Rahim Mia", phone = "01711122334", address = "Dhaka, Bangladesh", email = "rahim@example.com")
                    val customer2 = Customer(name = "Karim Uddin", phone = "01822334455", address = "Chittagong, Bangladesh")
                    val c1Id = dao.insertCustomer(customer1).toInt()
                    val c2Id = dao.insertCustomer(customer2).toInt()

                    // Seed Inventory
                    dao.insertInventory(InventoryItem(name = "Cotton Italian Fabric", type = "Fabric", quantity = 150, unit = "Yards", costPrice = 300.0, sellingPrice = 500.0))
                    dao.insertInventory(InventoryItem(name = "Premium Buttons", type = "Button", quantity = 1000, unit = "Pieces", costPrice = 5.0, sellingPrice = 15.0))
                    dao.insertInventory(InventoryItem(name = "Nylon Zipper 6inch", type = "Zipper", quantity = 500, unit = "Pieces", costPrice = 12.0, sellingPrice = 25.0))

                    // Seed Orders
                    dao.insertOrder(TailorOrder(
                        orderNumber = "ORD-2024-001",
                        customerId = c1Id,
                        status = "Sewing",
                        orderDate = System.currentTimeMillis() - 86400000 * 2,
                        deliveryDate = System.currentTimeMillis() + 86400000 * 5,
                        totalAmount = 2500.0,
                        advancePayment = 1000.0,
                        dueAmount = 1500.0,
                        itemsJson = "C-1234"
                    ))

                    dao.insertWorker(Worker(name = "Ariful Islam", role = "Cutting Master", phone = "0199988877", salary = 25000.0))
                    dao.insertWorker(Worker(name = "Rahat Sheikh", role = "Tailor", phone = "0188877766", salary = 20000.0))
                    
                    dao.insertOrder(TailorOrder(
                        orderNumber = "ORD-2024-002",
                        customerId = c2Id,
                        status = "Pending",
                        orderDate = System.currentTimeMillis(),
                        deliveryDate = System.currentTimeMillis() + 86400000 * 7,
                        totalAmount = 4500.0,
                        advancePayment = 2000.0,
                        dueAmount = 2500.0,
                        itemsJson = "T-456, B-999"
                    ))
                }
            }
        }
    }
}
