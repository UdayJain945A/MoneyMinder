package com.example.moneyminder.presentation.transactions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.model.Category
import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val preferenceManager: PreferenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _amount = mutableStateOf("")
    val amount: State<String> = _amount

    private val _note = mutableStateOf("")
    val note: State<String> = _note

    private val _type = mutableStateOf(TransactionType.EXPENSE)
    val type: State<TransactionType> = _type

    private val _category = mutableStateOf(Category.FOOD)
    val category: State<Category> = _category

    private val _currency = mutableStateOf("USD")
    val currency: State<String> = _currency

    private var currentTransactionId: Int? = null
    private var transactionDate: Long = System.currentTimeMillis()

    val isEditing: Boolean get() = currentTransactionId != null

    init {
        viewModelScope.launch {
            _currency.value = preferenceManager.currency.first()
        }

        savedStateHandle.get<Int>("transactionId")?.let { id ->
            if (id != -1) {
                viewModelScope.launch {
                    repository.getTransactionById(id)?.also { transaction ->
                        currentTransactionId = transaction.id
                        _amount.value = transaction.amount.toString()
                        _note.value = transaction.note
                        _type.value = transaction.type
                        _category.value = transaction.category
                        transactionDate = transaction.date
                    }
                }
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.toDoubleOrNull() != null) {
            _amount.value = newAmount
        }
    }

    fun onNoteChange(newNote: String) {
        _note.value = newNote
    }

    fun onTypeChange(newType: TransactionType) {
        _type.value = newType
        // Reset category if it's not valid for the new type
        if (newType == TransactionType.INCOME) {
            if (_category.value !in listOf(Category.SALARY, Category.GIFT, Category.INVESTMENT, Category.OTHERS)) {
                _category.value = Category.SALARY
            }
        } else {
            if (_category.value in listOf(Category.SALARY, Category.GIFT, Category.INVESTMENT)) {
                _category.value = Category.FOOD
            }
        }
    }

    fun onCategoryChange(newCategory: Category) {
        _category.value = newCategory
    }

    fun saveTransaction() {
        val amountDouble = _amount.value.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    id = currentTransactionId ?: 0,
                    amount = amountDouble,
                    type = _type.value,
                    category = _category.value,
                    date = transactionDate,
                    note = _note.value
                )
            )
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            currentTransactionId?.let { id ->
                repository.deleteTransaction(
                    Transaction(
                        id = id,
                        amount = 0.0, // Values other than ID don't matter for deletion in Room if it's based on ID, 
                                     // but let's be safe and provide a valid object if needed.
                        type = _type.value,
                        category = _category.value,
                        date = transactionDate,
                        note = _note.value
                    )
                )
            }
        }
    }
}
