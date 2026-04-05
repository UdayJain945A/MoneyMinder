package com.example.moneyminder.presentation.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.model.Budget
import com.example.moneyminder.domain.model.Category
import com.example.moneyminder.domain.model.Expense
import com.example.moneyminder.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state: StateFlow<ExpenseState> = _state.asStateFlow()

    init {
        getExpenses()
        getBudget()
        calculateTotalSpending()
        getCurrency()
    }

    private fun getCurrency() {
        preferenceManager.currency
            .onEach { currency ->
                _state.update { it.copy(currency = currency) }
            }
            .launchIn(viewModelScope)
    }

    private fun getExpenses() {
        repository.getAllExpenses()
            .onEach { expenses ->
                _state.update { it.copy(expenses = expenses) }
            }
            .launchIn(viewModelScope)
    }

    private fun getBudget() {
        repository.getBudget()
            .onEach { budget ->
                _state.update { it.copy(budget = budget?.monthlyLimit ?: 0.0) }
            }
            .launchIn(viewModelScope)
    }

    private fun calculateTotalSpending() {
        // Simple implementation: total spending for current month
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startOfMonth = calendar.timeInMillis
        
        repository.getTotalSpending(startOfMonth, System.currentTimeMillis())
            .onEach { total ->
                _state.update { it.copy(totalSpending = total ?: 0.0) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.AddExpense -> {
                viewModelScope.launch {
                    repository.insertExpense(event.expense)
                }
            }
            is ExpenseEvent.DeleteExpense -> {
                viewModelScope.launch {
                    repository.deleteExpense(event.expense)
                }
            }
            is ExpenseEvent.SetBudget -> {
                viewModelScope.launch {
                    repository.insertBudget(Budget(monthlyLimit = event.amount))
                }
            }
        }
    }
}

sealed class ExpenseEvent {
    data class AddExpense(val expense: Expense) : ExpenseEvent()
    data class DeleteExpense(val expense: Expense) : ExpenseEvent()
    data class SetBudget(val amount: Double) : ExpenseEvent()
}
