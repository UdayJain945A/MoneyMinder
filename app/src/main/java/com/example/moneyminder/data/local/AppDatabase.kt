package com.example.moneyminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneyminder.domain.model.Budget
import com.example.moneyminder.domain.model.Expense
import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.Goal

@Database(
    entities = [Expense::class, Budget::class, Transaction::class, Goal::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val expenseDao: ExpenseDao
    abstract val budgetDao: BudgetDao
    abstract val transactionDao: TransactionDao
    abstract val goalDao: GoalDao

    companion object {
        const val DATABASE_NAME = "money_minder_db"
    }
}
