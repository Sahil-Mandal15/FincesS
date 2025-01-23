package com.sahilm.fincess.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    var type: String = "",
    var category: Category? = null,
    var account: Account? = null,
    var note: String = "",
    var date: Date = Date(),
    var amount: Float = 0.0f,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
)
