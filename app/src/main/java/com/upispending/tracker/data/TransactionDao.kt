package com.upispending.tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("SELECT * FROM upi_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM upi_transactions WHERE upiApp = :app ORDER BY timestamp DESC")
    fun getTransactionsByApp(app: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM upi_transactions WHERE isDebit = 1")
    fun getTotalSpent(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM upi_transactions WHERE upiApp = :app AND isDebit = 1")
    fun getTotalSpentByApp(app: String): Flow<Double?>

    @Query("SELECT DISTINCT upiApp FROM upi_transactions")
    fun getDistinctApps(): Flow<List<String>>

    @Query("DELETE FROM upi_transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM upi_transactions")
    suspend fun clearAll()
}
