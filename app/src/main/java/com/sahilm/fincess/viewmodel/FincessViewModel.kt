package com.sahilm.fincess.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.sahilm.fincess.db.AppDatabase
import com.sahilm.fincess.db.TransactionDao
import com.sahilm.fincess.model.Transaction
import com.sahilm.fincess.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class FincessViewModel @Inject constructor(
    application: Application
): ViewModel() {

    private val transactionDao: TransactionDao = AppDatabase.getDatabase(application).transactionDao()

    private val _transaction = MutableStateFlow<List<Transaction>>(emptyList())
    val transaction: StateFlow<List<Transaction>> = _transaction.asStateFlow()
    private val _categoryTransaction = MutableStateFlow<List<Transaction>>(emptyList())
    val categoryTransaction: StateFlow<List<Transaction>> = _categoryTransaction.asStateFlow()
    private val _totalIncome = MutableStateFlow<List<Double>>(emptyList())
    val totalIncome: StateFlow<List<Double>> = _totalIncome
    private val _totalExpense = MutableStateFlow<List<Double>>(emptyList())
    val totalExpense: StateFlow<List<Double>> = _totalExpense
    private val _totalBalance = MutableStateFlow<List<Double>>(emptyList())
    val totalBalance: StateFlow<List<Double>> = _totalBalance

    fun getTransaction(calendar: Calendar, type: String) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        viewModelScope.launch(Dispatchers.IO) {
            val newTransactionFlow = when (Constants.SELECTED_TAB_STATUS) {
                Constants.DAILY -> transactionDao.getTransactionsByDateAndType(
                    calendar.time,
                    Date(calendar.time.time + (24 * 60 * 60 * 1000)),
                    type
                )
                Constants.MONTHLY -> {
                    calendar.set(Calendar.DAY_OF_MONTH, 0)
                    val startTime = calendar.time
                    calendar.add(Calendar.MONTH, 1)
                    val endTime = calendar.time
                    transactionDao.getTransactionsByDateAndType(
                        startTime, endTime, type
                    )
                }
                else -> flowOf(emptyList())
            }
            newTransactionFlow.collect { newtransaction ->
                _categoryTransaction.value = newtransaction
                println("FincessViewModel" + "categoryTransaction : $newtransaction")
            }
        }
    }


    fun getTransaction(calendar: Calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        viewModelScope.launch(Dispatchers.IO) {
            val newTransactionFlow = when (Constants.SELECTED_TAB_STATUS) {
                Constants.DAILY -> {
                    transactionDao.getTransactionsByDate(
                        calendar.time,
                        Date(calendar.time.time + (24 * 60 * 60 * 1000))
                    )
                }
                Constants.MONTHLY -> {
                    calendar.set(Calendar.DAY_OF_MONTH, 0)
                    val startTime = calendar.time
                    calendar.add(Calendar.MONTH, 1)
                    val endTime = calendar.time
                    transactionDao.getTransactionsByDate(startTime, endTime)
                }
                else -> {
                    flowOf(emptyList())
                }
            }

            newTransactionFlow.collect { newTransactions ->
                _transaction.value = newTransactions
                calculateTransactionSummary(calendar)
            }
        }
    }

    private fun calculateTransactionSummary(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        var income: Flow<List<Double>>
        var expense: Flow<List<Double>>
        var balance: Flow<List<Double>>

        viewModelScope.launch(Dispatchers.IO) {
            when (Constants.SELECTED_TAB_STATUS) {
                Constants.DAILY -> {
                    income = transactionDao.getTotalAmountByDateAndType(
                        calendar.time,
                        Date(calendar.time.time + (24 * 60 * 60 * 1000)),
                        Constants.INCOME
                    )
                    expense = transactionDao.getTotalAmountByDateAndType(
                        calendar.time,
                        Date(calendar.time.time + (24 * 60 * 60 * 1000)),
                        Constants.EXPENSE
                    )
                    balance = transactionDao.getTotalAmountByDate(
                        calendar.time,
                        Date(calendar.time.time + (24 * 60 * 60 * 1000))
                    )
                }
                Constants.MONTHLY -> {
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    val startTime = calendar.time
                    calendar.add(Calendar.MONTH, 1)
                    val endTime = calendar.time
                    income = transactionDao.getTotalAmountByDateAndType(
                        startTime, endTime, Constants.INCOME
                    )
                    expense = transactionDao.getTotalAmountByDateAndType(
                        startTime, endTime, Constants.EXPENSE
                    )
                    balance = transactionDao.getTotalAmountByDate(
                        startTime,endTime
                    )
                }
                else -> {
                    income = flowOf(emptyList())
                    expense = flowOf(emptyList())
                    balance = flowOf(emptyList())
                }
            }

            withContext(Dispatchers.Main){
                launch{
                    income.collect { newIncome ->
                        _totalIncome.value = newIncome
                        println("FincessViewModel" + " newIncome: $newIncome")
                    }
                }
                launch{
                    expense.collect { newExpense ->
                        _totalExpense.value = newExpense
                        println("FincessViewModel" + " newExpense: $newExpense")
                    }
                }
                launch{
                    balance.collect { newBalance ->
                        _totalBalance.value = newBalance
                        println("FincessViewModel" + " newBalance: $newBalance")
                    }
                }
            }
        }

       }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
                transactionDao.insertTransaction(transaction)
                println("FincessViewModel" + "addTransaction: transaction inserted $transaction")

                withContext(Dispatchers.Main){
                    val calendar = Calendar.getInstance()
                    getTransaction(calendar)
                    calculateTransactionSummary(calendar)
                }
        }


    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)

            withContext(Dispatchers.Main){
                val calendar = Calendar.getInstance()
                getTransaction(calendar)
                calculateTransactionSummary(calendar)
            }
        }
    }

    fun getPieEntries() = channelFlow {

        val pieEntries = mutableListOf<PieEntry>()


        val job = launch {
            categoryTransaction.collect { transactions ->
                val categoryMap: MutableMap<String, Float> = mutableMapOf()

                for (transaction in transactions) {
                    val category = transaction.category?.categoryName ?: "Unknown"
                    val amount = transaction.amount

                    categoryMap[category] = categoryMap.getOrDefault(category, 0.0f) + abs(amount)
                }

                for ((category, amount) in categoryMap) {
                    pieEntries.add(PieEntry(amount, category))
                }

                send(pieEntries)
            }
        }

        awaitClose { job.cancel() }

    }
}