package com.example.moneyminder.data.repository

import com.example.moneyminder.data.local.TransactionDao
import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import com.example.moneyminder.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> = dao.getAllTransactions()

    override suspend fun getTransactionById(id: Int): Transaction? = dao.getTransactionById(id)

    override suspend fun insertTransaction(transaction: Transaction) = dao.insertTransaction(transaction)

    override suspend fun updateTransaction(transaction: Transaction) = dao.updateTransaction(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) = dao.deleteTransaction(transaction)

    override fun getTotalByType(type: TransactionType): Flow<Double?> = dao.getTotalByType(type)

    override fun searchTransactions(query: String): Flow<List<Transaction>> = dao.searchTransactions(query)

    override suspend fun getLastTransaction(): Transaction? = dao.getLastTransaction()
}
