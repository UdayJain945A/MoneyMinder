package com.example.moneyminder.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transactions : Screen("transactions", "History", Icons.Default.History)
    object AddEditTransaction : Screen("add_edit_transaction", "Add", Icons.Default.Add)
    object Insights : Screen("insights", "Insights", Icons.Default.BarChart)
    object Goal : Screen("goal", "Goal", Icons.Default.TrackChanges)
    object Budget : Screen("budget", "Budget", Icons.Default.AccountBalanceWallet)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Transactions,
    Screen.Insights,
    Screen.Goal
)
