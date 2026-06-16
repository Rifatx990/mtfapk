package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.data.InventoryItem
import com.example.ui.MainViewModel
import com.example.utils.PrintHelper

@Composable
fun InventoryScreen(viewModel: MainViewModel, inventory: List<InventoryItem>) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredInventory = inventory.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.sku.contains(searchQuery, ignoreCase = true)
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("") }
        var cost by remember { mutableStateOf("") }
        var sku by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Inventory Item") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU / Barcode") })
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit (e.g., Yards)") })
                    OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Cost Price") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val qtyVal = quantity.toIntOrNull() ?: 0
                    val costVal = cost.toDoubleOrNull() ?: 0.0
                    viewModel.addInventoryItem(name, "General", qtyVal, unit, costVal, costVal * 1.5, sku)
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
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Stock Management", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = {
                    val html = "<html><body><h2>Inventory Report</h2><ul>" + filteredInventory.joinToString("") { "<li>${it.name} - ${it.sku} - ${it.quantity} ${it.unit}</li>" } + "</ul></body></html>"
                    PrintHelper.printText(context, html, "Stock_Report")
                }) {
                    Icon(Icons.Default.Print, contentDescription = "Print Inventory")
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search by name or SKU") },
                leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode") },
                singleLine = true
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredInventory) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                        Text("${item.quantity} ${item.unit}", modifier = Modifier.padding(horizontal = 4.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "SKU: ${item.sku.ifEmpty { "N/A" }} | Cost: ৳${item.costPrice} | Retail: ৳${item.sellingPrice}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row {
                                IconButton(onClick = {
                                    val html = "<html><body style='text-align: center; margin-top: 50px;'><h1>${item.name}</h1><h3>SKU: ${item.sku}</h3><img src='https://barcode.tec-it.com/barcode.ashx?data=${item.sku}&code=Code128&translate-esc=on' alt='Barcode'/></body></html>"
                                    PrintHelper.printText(context, html, "Label_${item.sku}")
                                }) {
                                    Icon(Icons.Default.Print, contentDescription = "Print Label", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteInventoryItem(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
