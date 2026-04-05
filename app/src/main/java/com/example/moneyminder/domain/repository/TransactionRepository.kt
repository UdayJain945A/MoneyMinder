package com.example.moneyminder.domain.repository

import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Int): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    fun getTotalByType(type: TransactionType): Flow<Double?>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    suspend fun getLastTransaction(): Transaction?
}
