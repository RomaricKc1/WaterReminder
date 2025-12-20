package com.romarickc.reminder.commons

import com.romarickc.reminder.domain.model.WaterIntake
import java.util.Calendar
import java.util.Date
import kotlin.collections.forEach
import kotlin.collections.set

class WaterIntakeData(
    waterIntakeList: List<WaterIntake>,
) {
    private val dayIntakeData = mutableMapOf<Int, Int>()
    private val monthIntakeData = mutableMapOf<Int, Int>()

    init {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        // days of the month
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (dayOfMonth in 1..maxDaysInMonth) {
            dayIntakeData[dayOfMonth] = 0
        }
        waterIntakeList.forEach {
            calendar.time = it.timestamp?.let { it1 -> Date(it1) }!!
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            if (year == currentYear && month == currentMonth) {
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                dayIntakeData[dayOfMonth] = dayIntakeData
                    .getOrDefault(dayOfMonth, 0) + 1
            }
        }

        // month of the year
        for (month in 1..12) {
            monthIntakeData[month] = 0
        }
        waterIntakeList.forEach {
            calendar.time = it.timestamp?.let { it1 -> Date(it1) }!!
            val year = calendar.get(Calendar.YEAR)
            if (year == currentYear) {
                val month = calendar.get(Calendar.MONTH) + 1
                monthIntakeData[month] = monthIntakeData
                    .getOrDefault(month, 0) + 1
            }
        }
    }

    fun getDayIntakeData(): Map<Int, Int> = dayIntakeData

    fun getMonthIntakeData(): Map<Int, Int> = monthIntakeData
}
