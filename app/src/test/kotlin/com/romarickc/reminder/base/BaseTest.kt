package com.romarickc.reminder.base

import com.romarickc.reminder.commons.averageToDay
import com.romarickc.reminder.commons.averageToMonth
import com.romarickc.reminder.commons.getTimeTxt
import com.romarickc.reminder.commons.mapHeight
import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BaseTest {
    private val init = 41
    private val mapData: Map<Int, Int> =
        mapOf(
            1 to 2,
            2 to 3,
            3 to 1,
            4 to 4,
            5 to 2,
            6 to 3,
            7 to 7,
            8 to 10,
        )

    @Test
    fun add_test() {
        assertEquals(3, 2 + 1)
    }

    @Test
    fun `this is a simple basic test`() {
        val anything = 31

        assertEquals(init, anything + 10)
        assertNotEquals(init, anything)
    }

    @Test
    fun `map functionality`() {
        val res =
            mapHeight(
                21f,
                0f,
                100f,
                100f,
                380f,
            )
        assertEquals(res, 158.8f)
    }

    @Test
    fun `averageToDay functionality`() {
        val currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val sumUpToToday =
            (1 until currentDayOfMonth + 1).sumOf { index ->
                mapData.getOrDefault(index, 0)
            }
        val res = averageToDay(mapData)

        assertEquals(res, (sumUpToToday.toFloat() / currentDayOfMonth))
    }

    @Test
    fun `averageToMonth functionality`() {
        val res = averageToMonth(mapData)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val sumUpToMonth =
            (1 until currentMonth + 1).sumOf { index ->
                mapData.getOrDefault(index, 0)
            }

        assertEquals(res, (sumUpToMonth.toFloat() / currentMonth))
    }

    /*@Test
    fun `GetTimeAgo functionality`() {
        val currentTime = System.currentTimeMillis()
        val twoMins = 2 * MIN_SEC * SEC_MS
        var res = getTimeAgo(currentTime - twoMins)
        assertEquals(res, "2 mins ago")

        val twoHours = 2 * HOUR_SEC * SEC_MS
        res = getTimeAgo(currentTime - twoHours)
        assertEquals(res, "2 hours ago")

        val oneYear = YEAR_SEC * SEC_MS
        res = getTimeAgo(currentTime - oneYear)
        assertEquals(res, "1 year ago")
    }*/

    @Test
    fun `getTimeTxt functionality`() {
        // dd/MM/yyyy HH:mm:ss
        // Friday, March 31, 2023 7:18:08 PM
        val res = getTimeTxt(1680290288000, explicitUTC = true)
        assertEquals(res, "31/03/2023 19:18:08")
    }
}
