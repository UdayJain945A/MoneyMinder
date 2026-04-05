package com.example.moneyminder.data.local

import androidx.room.*
import com.example.moneyminder.domain.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE month = :month AND year = :year LIMIT 1")
    fun getGoalByMonth(month: Int, year: Int): Flow<Goal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}
