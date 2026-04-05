package com.example.moneyminder.domain.repository

import com.example.moneyminder.domain.model.Budget
import com.example.moneyminder.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(id: Int): Expense?
    suspend fun insertExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double?>
    
    fun getBudget(): Flow<Budget?>
    suspend fun insertBudget(budget: Budget)
}
