package com.sahilm.fincess.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sahilm.fincess.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date < :endDate AND type = :type")
    fun getTransactionsByDateAndType(startDate: Date, endDate: Date, type: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date < :endDate")
    fun getTransactionsByDate(startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date >= :startDate AND date < :endDate AND type = :type ")
    fun getTotalAmountByDateAndType(startDate: Date, endDate: Date, type: String): Flow<List<Double>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date >= :startDate AND date < :endDate")
    fun getTotalAmountByDate(startDate: Date, endDate: Date): Flow<List<Double>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}