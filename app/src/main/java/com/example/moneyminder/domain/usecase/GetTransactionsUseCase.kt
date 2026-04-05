package com.example.moneyminder.domain.usecase

import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()
}
