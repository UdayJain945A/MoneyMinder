package com.example.moneyminder.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.repository.TransactionRepository
import com.example.moneyminder.domain.repository.GoalRepository
import com.example.moneyminder.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val expenseRepository: ExpenseRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        combine(
            transactionRepository.getTotalByType(TransactionType.INCOME),
            transactionRepository.getTotalByType(TransactionType.EXPENSE),
            transactionRepository.getAllTransactions(),
            goalRepository.getGoalByMonth(month, year),
            expenseRepository.getBudget().combine(preferenceManager.currency) { b, c -> b to c }
        ) { income, expense, transactions, goal, budgetWithCurrency ->
            val (budget, currency) = budgetWithCurrency
            val totalIncome = income ?: 0.0
            val totalExpense = expense ?: 0.0
            
            // Calculate No-Spend Streak
            val streak = calculateNoSpendStreak(transactions)
            
            // Weekly spending for chart (last 7 days)
            val weeklySpending = calculateWeeklySpending(transactions)

            // Current Month Spending
            val currentMonthTransactions = transactions.filter {
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.date
                cal.get(Calendar.MONTH) + 1 == month && 
                cal.get(Calendar.YEAR) == year
            }
            
            val currentMonthIncome = currentMonthTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
                
            val currentMonthExpense = currentMonthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            HomeState(
                balance = totalIncome - totalExpense,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                recentTransactions = transactions.take(5),
                savingsGoal = goal?.targetAmount ?: 0.0,
                savingsProgress = if (goal != null && goal.targetAmount > 0) {
                    (currentMonthIncome - currentMonthExpense).coerceAtLeast(0.0) / goal.targetAmount
                } else 0.0,
                noSpendStreak = streak,
                weeklySpending = weeklySpending,
                monthlyBudget = budget?.monthlyLimit ?: 0.0,
                currentMonthExpense = currentMonthExpense,
                currency = currency
            )
        }.onEach { newState ->
            _state.value = newState
        }.launchIn(viewModelScope)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    private fun calculateNoSpendStreak(transactions: List<Transaction>): Int {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            .map { 
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.date
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }.toSet()

        var streak = 0
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        // Check back from today
        while (true) {
            if (!expenses.contains(cal.timeInMillis)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
            if (streak > 365) break // Safety
        }
        return streak
    }

    private fun calculateWeeklySpending(transactions: List<Transaction>): List<Double> {
        val last7Days = (0..6).map { i ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.reversed()

        return last7Days.map { timestamp ->
            transactions.filter { 
                it.type == TransactionType.EXPENSE && 
                it.date >= timestamp && 
                it.date < timestamp + TimeUnit.DAYS.toMillis(1) 
            }.sumOf { it.amount }
        }
    }
}

data class HomeState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val savingsGoal: Double = 0.0,
    val savingsProgress: Double = 0.0,
    val noSpendStreak: Int = 0,
    val weeklySpending: List<Double> = emptyList(),
    val monthlyBudget: Double = 0.0,
    val currentMonthExpense: Double = 0.0,
    val currency: String = "INR"
)
