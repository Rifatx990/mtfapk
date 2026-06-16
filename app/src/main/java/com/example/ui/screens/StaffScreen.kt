package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.Worker
import com.example.ui.MainViewModel
import com.example.utils.PrintHelper

@Composable
fun StaffScreen(viewModel: MainViewModel, workers: List<Worker>) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredWorkers = workers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.role.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery, ignoreCase = true)
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var salary by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Staff") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                    OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Salary") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val salaryVal = salary.toDoubleOrNull() ?: 0.0
                    viewModel.addWorker(name, role, phone, salaryVal)
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
                Icon(Icons.Default.Add, contentDescription = "Add Staff")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Staff Roster", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = {
                    val html = "<html><body><h2>Staff List</h2><ul>" + filteredWorkers.joinToString("") { "<li>${it.name} - ${it.role} - ${it.phone}</li>" } + "</ul></body></html>"
                    PrintHelper.printText(context, html, "Staff_Roster")
                }) {
                    Icon(Icons.Default.Print, contentDescription = "Print Staff List")
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search by name, role or phone") },
                singleLine = true
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredWorkers) { worker ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(worker.name, style = MaterialTheme.typography.titleMedium)
                                Text(worker.role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                Text("Phone: ${worker.phone} | Salary: ৳${worker.salary}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.deleteWorker(worker) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
