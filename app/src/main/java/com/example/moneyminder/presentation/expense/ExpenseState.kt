package com.example.moneyminder.presentation.expense

import com.example.moneyminder.domain.model.Expense

data class ExpenseState(
    val expenses: List<Expense> = emptyList(),
    val totalSpending: Double = 0.0,
    val budget: Double = 0.0,
    val currency: String = "USD",
    val isLoading: Boolean = false
)
