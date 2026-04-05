package com.example.moneyminder.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.model.Category
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    init {
        combine(
            repository.getAllTransactions(),
            preferenceManager.currency,
            _state
        ) { transactions, currency, currentState ->
            val filtered = transactions.filter { transaction ->
                val matchesQuery = transaction.note.contains(currentState.searchQuery, ignoreCase = true) ||
                        transaction.category.title.contains(currentState.searchQuery, ignoreCase = true)
                val matchesType = currentState.selectedType == null || transaction.type == currentState.selectedType
                val matchesCategory = currentState.selectedCategory == null || transaction.category == currentState.selectedCategory

                matchesQuery && matchesType && matchesCategory
            }
            currentState.copy(
                allTransactions = transactions,
                transactions = filtered,
                currency = currency,
                isLoading = false
            )
        }
        .onEach { newState ->
            _state.value = newState
        }
        .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onTypeFilterChange(type: TransactionType?) {
        _state.update { it.copy(selectedType = type) }
    }

    fun onCategoryFilterChange(category: Category?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun deleteTransaction(transaction: com.example.moneyminder.domain.model.Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}

data class TransactionListState(
    val isLoading: Boolean = true,
    val transactions: List<com.example.moneyminder.domain.model.Transaction> = emptyList(),
    val allTransactions: List<com.example.moneyminder.domain.model.Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategory: Category? = null,
    val currency: String = "USD"
)
