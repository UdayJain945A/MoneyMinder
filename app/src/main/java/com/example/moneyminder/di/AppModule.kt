package com.example.moneyminder.di

import android.content.Context
import androidx.room.Room
import com.example.moneyminder.data.local.AppDatabase
import com.example.moneyminder.data.local.BudgetDao
import com.example.moneyminder.data.local.ExpenseDao
import com.example.moneyminder.data.local.GoalDao
import com.example.moneyminder.data.local.TransactionDao
import com.example.moneyminder.data.repository.ExpenseRepositoryImpl
import com.example.moneyminder.data.repository.GoalRepositoryImpl
import com.example.moneyminder.data.repository.TransactionRepositoryImpl
import com.example.moneyminder.domain.repository.ExpenseRepository
import com.example.moneyminder.domain.repository.GoalRepository
import com.example.moneyminder.domain.repository.TransactionRepository
import com.example.moneyminder.util.VoiceToTextParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.app.Application

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVoiceToTextParser(app: Application): VoiceToTextParser {
        return VoiceToTextParser(app)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao

    @Provides
    @Singleton
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao

    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao

    @Provides
    @Singleton
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao

    @Provides
    @Singleton
    fun provideExpenseRepository(expenseDao: ExpenseDao, budgetDao: BudgetDao): ExpenseRepository {
        return ExpenseRepositoryImpl(expenseDao, budgetDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(transactionDao)
    }

    @Provides
    @Singleton
    fun provideGoalRepository(goalDao: GoalDao): GoalRepository {
        return GoalRepositoryImpl(goalDao)
    }


}
