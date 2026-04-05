package com.example.moneyminder.domain.usecase

import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.deleteTransaction(transaction)
    }
}
