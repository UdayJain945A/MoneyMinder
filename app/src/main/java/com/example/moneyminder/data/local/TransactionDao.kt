package com.example.moneyminder.data.local

import androidx.room.*
import com.example.moneyminder.domain.model.Transaction
import com.example.moneyminder.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalByType(type: TransactionType): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE amount >= :minAmount AND amount <= :maxAmount")
    fun filterByAmount(minAmount: Double, maxAmount: Double): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%'")
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 1")
    suspend fun getLastTransaction(): Transaction?
}
