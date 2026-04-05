package com.example.moneyminder.presentation.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyminder.presentation.expense.ExpenseViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onPopBackStack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Analytics") },
                navigationIcon = {
                    IconButton(onClick = onPopBackStack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Total Spending",
                    style = MaterialTheme.typography.titleLarge
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", state.totalSpending)}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Weekly Spending Trend",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Show recent expenses as a simple list
                if (state.expenses.isEmpty()) {
                    Text("No expenses yet", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.expenses.take(7).forEachIndexed { _, expense ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = expense.category.title,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = expense.title,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", expense.amount)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Category Breakdown",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category breakdown
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.expenses.groupBy { it.category }.forEach { (category, expenses) ->
                        val total = expenses.sumOf { it.amount }
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = category.title)
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", total)}",
                                    color = category.color
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (total / state.totalSpending.coerceAtLeast(1.0)).toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = category.color
                            )
                        }
                    }
                }
            }
        }
    }
}
