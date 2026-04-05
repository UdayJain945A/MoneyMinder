package com.example.moneyminder.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.model.Goal
import com.example.moneyminder.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(GoalState())
    val state: StateFlow<GoalState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        combine(
            repository.getGoalByMonth(month, year),
            preferenceManager.currency
        ) { goal, currency ->
            _state.update { it.copy(goal = goal, currency = currency, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun setGoal(amount: Double) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        viewModelScope.launch {
            repository.insertGoal(
                Goal(
                    targetAmount = amount,
                    month = month,
                    year = year
                )
            )
        }
    }
}

data class GoalState(
    val goal: Goal? = null,
    val currency: String = "USD",
    val isLoading: Boolean = true
)
