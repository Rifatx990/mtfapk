package com.example.data

import androidx.room.*
import java.util.Date

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val email: String = "",
    val notes: String = "",
    val joinDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class TailorOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val customerId: Int,
    val status: String,
    val orderDate: Long,
    val deliveryDate: Long,
    val totalAmount: Double,
    val advancePayment: Double,
    val dueAmount: Double,
    val itemsJson: String = "[]"
)

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // Fabric, Button, Thread
    val quantity: Int,
    val unit: String,
    val costPrice: Double,
    val sellingPrice: Double,
    val sku: String = ""
)

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val type: String, // Shirt, Pant, Panjabi
    val detailsJson: String // Serialized details
)

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String, // Cutting Master, Tailor, Manager
    val phone: String,
    val joinDate: Long = System.currentTimeMillis(),
    val salary: Double
)
