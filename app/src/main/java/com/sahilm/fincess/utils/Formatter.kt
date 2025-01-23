package com.sahilm.fincess.utils

import android.icu.text.SimpleDateFormat
import java.util.Date

class Formatter {

    private lateinit var sdf: SimpleDateFormat

    fun formatDate(date: Date): String {
        sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(date)
    }

    fun formatDateByMonth(date: Date): String {
        sdf = SimpleDateFormat("MM-yyyy")
        return sdf.format(date)
    }

}