package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Customer
import com.example.data.InventoryItem
import com.example.data.TailorDao
import com.example.data.TailorOrder
import com.example.data.Worker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: TailorDao = AppDatabase.getDatabase(application).tailorDao()

    val customers = dao.getAllCustomers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val orders = dao.getAllOrders().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val inventory = dao.getAllInventory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val workers = dao.getAllWorkers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addCustomer(name: String, phone: String, address: String) {
        viewModelScope.launch {
            dao.insertCustomer(Customer(name = name, phone = phone, address = address))
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            dao.deleteCustomer(customer)
        }
    }

    fun addOrder(orderNumber: String, customerId: Int, total: Double, advance: Double, itemsJson: String = "") {
        viewModelScope.launch {
            dao.insertOrder(TailorOrder(
                orderNumber = orderNumber,
                customerId = customerId,
                status = "Pending",
                orderDate = System.currentTimeMillis(),
                deliveryDate = System.currentTimeMillis() + 604800000L, // 1 week later
                totalAmount = total,
                advancePayment = advance,
                dueAmount = total - advance,
                itemsJson = itemsJson
            ))
        }
    }

    fun deleteOrder(order: TailorOrder) {
        viewModelScope.launch {
            dao.deleteOrder(order)
        }
    }

    fun updateOrderStatus(order: TailorOrder, newStatus: String) {
        viewModelScope.launch {
            dao.updateOrder(order.copy(status = newStatus))
        }
    }

    fun addInventoryItem(name: String, type: String, quantity: Int, unit: String, cost: Double, retail: Double, sku: String = "") {
        viewModelScope.launch {
            dao.insertInventory(InventoryItem(name = name, type = type, quantity = quantity, unit = unit, costPrice = cost, sellingPrice = retail, sku = sku))
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            dao.deleteInventory(item)
        }
    }

    fun updateInventoryItemQty(item: InventoryItem, newQty: Int) {
        viewModelScope.launch {
            dao.updateInventory(item.copy(quantity = newQty))
        }
    }

    fun addWorker(name: String, role: String, phone: String, salary: Double) {
        viewModelScope.launch {
            dao.insertWorker(Worker(name = name, role = role, phone = phone, salary = salary))
        }
    }

    fun deleteWorker(worker: Worker) {
        viewModelScope.launch {
            dao.deleteWorker(worker)
        }
    }
}
