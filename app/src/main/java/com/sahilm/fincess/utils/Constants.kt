package com.sahilm.fincess.utils

import com.sahilm.fincess.R
import com.sahilm.fincess.model.Account
import com.sahilm.fincess.model.Category

class Constants {

    companion object {
        const val INCOME = "Income"
        const val EXPENSE = "Expense"

        var SELECTED_TAB = 0
        var SELECTED_TAB_STATUS = 0
        var SELECTED_STATS_TYPE = INCOME
        const val DAILY = 0
        const val MONTHLY = 1
        const val CALENDAR = 2
        const val SUMMARY = 3
        const val NOTES = 4

        var categories: List<Category> = listOf(
            Category("Salary", R.drawable.ic_salary, R.color.category1),
            Category("Business", R.drawable.ic_business, R.color.category2),
            Category("Investment", R.drawable.ic_investment, R.color.category3),
            Category("Loan", R.drawable.ic_loan, R.color.category4),
            Category("Rent", R.drawable.ic_rent, R.color.category5),
            Category("Other", R.drawable.ic_other, R.color.category6)
        )

        val accounts: List<Account> = listOf(
            Account(0.0, "Cash"),
            Account(0.0, "Card"),
            Account(0.0, "UPI"),
            Account(0.0, "Other")
        )



        fun getCategoryDetails(categoryName: String): Category? {
            return categories.find { it.categoryName == categoryName }
        }

        fun getAccountsColor(accountName: String): Int {
            return when (accountName) {
                "UPI" -> R.color.bank_color
                "Cash" -> R.color.cash_color
                "Card" -> R.color.card_color
                else -> R.color.default_color
            }
        }
    }
}