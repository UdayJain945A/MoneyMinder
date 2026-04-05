package com.example.moneyminder.presentation.transactions

import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.model.Category

data class TransactionState(
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategory: Category? = null,
    val isLoading: Boolean = false
)
