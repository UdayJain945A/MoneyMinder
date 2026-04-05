package com.example.moneyminder.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.model.Category
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state.asStateFlow()

    init {
        repository.getAllTransactions()
            .combine(preferenceManager.currency) { transactions, currency ->
                val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
                val categorySpending = expenses.groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val highestCategory = categorySpending.maxByOrNull { it.value }?.key

                InsightsState(
                    categorySpending = categorySpending,
                    highestSpendingCategory = highestCategory,
                    currency = currency,
                    isLoading = false
                )
            }
            .onEach { newState -> _state.value = newState }
            .launchIn(viewModelScope)
    }
}

data class InsightsState(
    val categorySpending: Map<Category, Double> = emptyMap(),
    val highestSpendingCategory: Category? = null,
    val currency: String = "USD",
    val isLoading: Boolean = true
)
