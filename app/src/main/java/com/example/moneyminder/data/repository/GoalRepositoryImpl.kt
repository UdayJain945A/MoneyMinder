package com.example.moneyminder.data.repository

import com.example.moneyminder.data.local.GoalDao
import com.example.moneyminder.domain.model.Goal
import com.example.moneyminder.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {
    override fun getGoalByMonth(month: Int, year: Int): Flow<Goal?> = goalDao.getGoalByMonth(month, year)

    override suspend fun insertGoal(goal: Goal) = goalDao.insertGoal(goal)

    override suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)

    override suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
}
