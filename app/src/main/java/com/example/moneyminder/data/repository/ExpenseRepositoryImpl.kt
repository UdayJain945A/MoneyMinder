package com.example.moneyminder.data.repository

import com.example.moneyminder.data.local.BudgetDao
import com.example.moneyminder.data.local.ExpenseDao
import com.example.moneyminder.domain.model.Budget
import com.example.moneyminder.domain.model.Expense
import com.example.moneyminder.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
) : ExpenseRepository {
    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    override suspend fun getExpenseById(id: Int): Expense? = expenseDao.getExpenseById(id)

    override suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)

    override suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    override suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    override fun getTotalSpending(startDate: Long, endDate: Long): Flow<Double?> =
        expenseDao.getTotalSpending(startDate, endDate)

    override fun getBudget(): Flow<Budget?> = budgetDao.getBudget()

    override suspend fun insertBudget(budget: Budget) = budgetDao.insertBudget(budget)
}
