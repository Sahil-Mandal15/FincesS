package com.sahilm.fincess.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sahilm.fincess.model.Account
import com.sahilm.fincess.model.Category
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
       return date?.time
    }

    @TypeConverter
    fun fromCategory(category: Category?): String? {
        return Gson().toJson(category)
    }

    @TypeConverter
    fun toCategory(categoryString: String?): Category? {
        return Gson().fromJson(categoryString, Category::class.java)
    }

    @TypeConverter
    fun fromCategory(account: Account?): String? {
        return Gson().toJson(account)
    }

    @TypeConverter
    fun toAccount(accountString: String?): Account? {
        return Gson().fromJson(accountString, Account::class.java)
    }
}