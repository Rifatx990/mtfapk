package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.AiViewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.StaffScreen
import androidx.compose.runtime.collectAsState

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = viewModel(),
    aiViewModel: AiViewModel = viewModel()
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                val orders by mainViewModel.orders.collectAsState()
                DashboardScreen(orders = orders)
            }
            composable("stock") {
                val inventory by mainViewModel.inventory.collectAsState()
                InventoryScreen(viewModel = mainViewModel, inventory = inventory)
            }
            composable("customers") {
                val customers by mainViewModel.customers.collectAsState()
                CustomersScreen(viewModel = mainViewModel, customers = customers)
            }
            composable("staff") {
                val workers by mainViewModel.workers.collectAsState()
                StaffScreen(viewModel = mainViewModel, workers = workers)
            }
            composable("reports") {
                val orders by mainViewModel.orders.collectAsState()
                val customers by mainViewModel.customers.collectAsState()
                OrdersScreen(viewModel = mainViewModel, orders = orders, customers = customers)
            }
            composable("assistant") {
                AiAssistantScreen(aiViewModel = aiViewModel)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        "home" to Icons.Default.Home,
        "stock" to Icons.Default.Inventory2,
        "customers" to Icons.Default.Person,
        "staff" to Icons.Default.Group,
        "reports" to Icons.Default.BarChart
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        items.forEach { (route, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = route) },
                label = { Text(route.uppercase(), style = MaterialTheme.typography.labelSmall) },
                selected = currentRoute == route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
