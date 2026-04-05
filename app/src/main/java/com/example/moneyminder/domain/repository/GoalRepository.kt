package com.example.moneyminder.domain.repository

import com.example.moneyminder.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoalByMonth(month: Int, year: Int): Flow<Goal?>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
}
