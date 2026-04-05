package com.example.moneyminder.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneyminder.presentation.home.HomeScreen
import com.example.moneyminder.presentation.transactions.TransactionListScreen
import com.example.moneyminder.presentation.insights.InsightsScreen
import com.example.moneyminder.presentation.goal.GoalScreen
import com.example.moneyminder.presentation.transactions.AddEditTransactionScreen
import com.example.moneyminder.presentation.settings.SettingsScreen
import com.example.moneyminder.presentation.budget.BudgetScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddEditTransaction.route) },
                onNavigateToEditTransaction = { id ->
                    navController.navigate("${Screen.AddEditTransaction.route}?transactionId=$id")
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) }
            )
        }
        composable(Screen.Budget.route) {
            BudgetScreen(
                onPopBackStack = { navController.popBackStack() }
            )
        }
        composable(Screen.Transactions.route) {
            TransactionListScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddEditTransaction.route) },
                onNavigateToEditTransaction = { id -> 
                    navController.navigate("${Screen.AddEditTransaction.route}?transactionId=$id")
                }
            )
        }
        composable(Screen.Insights.route) {
            InsightsScreen()
        }
        composable(Screen.Goal.route) {
            GoalScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Screen.AddEditTransaction.route}?transactionId={transactionId}",
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddEditTransactionScreen(
                onPopBackStack = { navController.popBackStack() }
            )
        }
        // Redirect old route if necessary
        composable("add_edit_transaction") {
            AddEditTransactionScreen(
                onPopBackStack = { navController.popBackStack() }
            )
        }
    }
}
