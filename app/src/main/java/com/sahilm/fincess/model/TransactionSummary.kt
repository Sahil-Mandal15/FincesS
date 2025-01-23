package com.sahilm.fincess.model

data class TransactionSummary(
    val income: Double,
    val expense: Double,
    val balance: Double,
    val transaction: List<Transaction>
)
