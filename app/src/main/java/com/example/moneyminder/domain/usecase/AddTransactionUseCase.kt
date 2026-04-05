package com.example.moneyminder.domain.usecase

import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        if (transaction.amount <= 0) {
            throw Exception("Amount must be greater than zero")
        }
        repository.insertTransaction(transaction)
    }
}
