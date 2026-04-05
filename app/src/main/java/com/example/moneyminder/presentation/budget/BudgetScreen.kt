package com.example.moneyminder.presentation.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyminder.presentation.home.getCurrencySymbol
import com.example.moneyminder.presentation.expense.ExpenseEvent
import com.example.moneyminder.presentation.expense.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onPopBackStack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var budgetAmount by remember { mutableStateOf("") }

    LaunchedEffect(state.budget) {
        if (state.budget > 0) {
            budgetAmount = state.budget.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Monthly Budget") },
                navigationIcon = {
                    IconButton(onClick = onPopBackStack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Track your monthly spending by setting a budget limit.",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = budgetAmount,
                onValueChange = { budgetAmount = it },
                label = { Text("Monthly Budget Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text(getCurrencySymbol(state.currency)) }
            )

            Button(
                onClick = {
                    val amount = budgetAmount.toDoubleOrNull() ?: 0.0
                    viewModel.onEvent(ExpenseEvent.SetBudget(amount))
                    onPopBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Budget")
            }
        }
    }
}
