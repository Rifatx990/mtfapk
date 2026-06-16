package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import com.example.data.TailorOrder
import com.example.data.Customer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.MainViewModel
import com.example.utils.PrintHelper

@Composable
fun OrdersScreen(viewModel: MainViewModel, orders: List<TailorOrder>, customers: List<Customer>) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredOrders = orders.filter { order ->
        val customer = customers.find { it.id == order.customerId }
        order.orderNumber.contains(searchQuery, ignoreCase = true) ||
        (customer?.name?.contains(searchQuery, ignoreCase = true) ?: false) ||
        (customer?.phone?.contains(searchQuery, ignoreCase = true) ?: false)
    }

    if (showAddDialog) {
        var orderNum by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var advance by remember { mutableStateOf("") }
        var itemsJson by remember { mutableStateOf("") }
        var expandedCustomer by remember { mutableStateOf(false) }
        var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Order") },
            text = {
                Column {
                    Box {
                        OutlinedTextField(
                            value = selectedCustomer?.name ?: "Select Customer",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Customer") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedCustomer = true }
                        )
                        DropdownMenu(expanded = expandedCustomer, onDismissRequest = { expandedCustomer = false }) {
                            customers.forEach { customer ->
                                DropdownMenuItem(text = { Text("${customer.name} - ${customer.phone}") }, onClick = {
                                    selectedCustomer = customer
                                    expandedCustomer = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = orderNum, onValueChange = { orderNum = it }, label = { Text("Order Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = itemsJson, onValueChange = { itemsJson = it }, label = { Text("Scan Items (SKUs)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Total Amount") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = advance, onValueChange = { advance = it }, label = { Text("Advance Amount") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amountVal = amount.toDoubleOrNull() ?: 0.0
                    val advanceVal = advance.toDoubleOrNull() ?: 0.0
                    val customerId = selectedCustomer?.id ?: 1
                    viewModel.addOrder(orderNum, customerId, amountVal, advanceVal, itemsJson)
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Order")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Order Management", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = {
                    val html = "<html><body><h2>Order Report</h2><ul>" + filteredOrders.joinToString("") { 
                        val customer = customers.find { c -> c.id == it.customerId }
                        "<li>${it.orderNumber} - ${customer?.name ?: "Unknown"} - ${it.status} - ৳${it.totalAmount}</li>" 
                    } + "</ul></body></html>"
                    PrintHelper.printText(context, html, "Orders_Report")
                }) {
                    Icon(Icons.Default.Print, contentDescription = "Print Orders")
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search by Order No, Customer Name or Phone") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredOrders) { order ->
                    val customer = customers.find { it.id == order.customerId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = order.orderNumber, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${customer?.name ?: "Unknown"} (${customer?.phone ?: "No Phone"})", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    var expanded by remember { mutableStateOf(false) }
                                    Box {
                                        TextButton(onClick = { expanded = true }) {
                                            Text(order.status)
                                        }
                                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                            DropdownMenuItem(text = { Text("Pending") }, onClick = { viewModel.updateOrderStatus(order, "Pending"); expanded = false })
                                            DropdownMenuItem(text = { Text("Cutting") }, onClick = { viewModel.updateOrderStatus(order, "Cutting"); expanded = false })
                                            DropdownMenuItem(text = { Text("Sewing") }, onClick = { viewModel.updateOrderStatus(order, "Sewing"); expanded = false })
                                            DropdownMenuItem(text = { Text("Ready") }, onClick = { viewModel.updateOrderStatus(order, "Ready"); expanded = false })
                                            DropdownMenuItem(text = { Text("Delivered") }, onClick = { viewModel.updateOrderStatus(order, "Delivered"); expanded = false })
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = {
                                        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                        val invoiceHtml = """
                                            <html>
                                                <head>
                                                    <style>
                                                        body { font-family: sans-serif; padding: 20px; }
                                                        .header { text-align: center; margin-bottom: 20px; border-bottom: 1px solid #000; padding-bottom: 10px; }
                                                        .details { margin-bottom: 20px; }
                                                        .footer { margin-top: 50px; display: flex; justify-content: space-between; }
                                                        .signature { border-top: 1px solid #000; width: 150px; text-align: center; padding-top: 5px; }
                                                    </style>
                                                </head>
                                                <body>
                                                    <div class="header">
                                                        <h1>Mehedi Tailors And Fabrics</h1>
                                                        <p>Yusuf market, Dhonaid, Ashulia, Savar, Dhaka -1341</p>
                                                        <p>Mob: 01812249596, 01720267213</p>
                                                    </div>
                                                    <div class="details">
                                                        <h2>INVOICE</h2>
                                                        <p><strong>Order No:</strong> ${order.orderNumber}</p>
                                                        <p><strong>Date:</strong> ${sdf.format(Date(order.orderDate))}</p>
                                                        <p><strong>Delivery by:</strong> ${sdf.format(Date(order.deliveryDate))}</p>
                                                        <p><strong>Customer:</strong> ${customer?.name ?: "Unknown"}</p>
                                                        <p><strong>Phone:</strong> ${customer?.phone ?: ""}</p>
                                                        <p><strong>Items (SKUs):</strong> ${order.itemsJson}</p>
                                                        <hr>
                                                        <p><strong>Total Amount:</strong> ৳${order.totalAmount}</p>
                                                        <p><strong>Advance Payment:</strong> ৳${order.advancePayment}</p>
                                                        <p><strong>Due Amount:</strong> ৳${order.dueAmount}</p>
                                                    </div>
                                                    <div class="footer">
                                                        <div class="signature">Buyer Signature</div>
                                                        <div class="signature">Seller Signature</div>
                                                    </div>
                                                </body>
                                            </html>
                                        """.trimIndent()
                                        PrintHelper.printText(context, invoiceHtml, "Invoice_${order.orderNumber}")
                                    }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Print, contentDescription = "Print Invoice", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.deleteOrder(order) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                Text(text = "Order Date: ${sdf.format(Date(order.orderDate))}", style = MaterialTheme.typography.labelMedium)
                                Text(text = "Delivery by: ${sdf.format(Date(order.deliveryDate))}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (order.itemsJson.isNotEmpty() && order.itemsJson != "[]") {
                                Text(text = "Scanned Items: ${order.itemsJson}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Total: ৳${order.totalAmount}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Due: ৳${order.dueAmount}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
