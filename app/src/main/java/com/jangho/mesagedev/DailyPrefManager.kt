package com.jangho.mesagedev

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyPrefManager(context: Context) {
    private val PREF_NAME = "daily_preferences"
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    // 값을 초기화하는 메서드
    fun resetDailyValue() : Boolean {
        val currentDate = dateFormat.format(Date()).toInt().toString()
        // 오늘 날짜와 저장된 날짜가 다르다면 값을 초기화하고 저장
        if (currentDate != sharedPreferences.getString(KEY_DATE, "0")) {
            sharedPreferences.edit().apply {
                putString(KEY_DATE, currentDate)
                apply()
            }
            return true
        }

        return false
    }


    companion object {
        private const val KEY_DATE = "last_reset_date"
    }
}