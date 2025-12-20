package com.romarickc.reminder.base

import com.romarickc.reminder.base.data.repository.TestRepository
import com.romarickc.reminder.domain.model.WaterIntake
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRepositoryFunctionality {
    private lateinit var testRepository: TestRepository

    @BeforeTest
    fun setup() {
        testRepository = TestRepository()

        val list =
            listOf(
                WaterIntake(1, 1680290288000), // March 31, 2023
                WaterIntake(2, 1680376688000), // April 1, 2023
                WaterIntake(3, 1680463088000), // April 2, 2023
                WaterIntake(4, 1680549488000), // April 3, 2023
                WaterIntake(5, 1680635888000), // April 4, 2023
                WaterIntake(6, 1680722288000), // April 5, 2023
                WaterIntake(7, 1680808688000), // April 6, 2023
                WaterIntake(8, 1680895088000), // April 7, 2023
                WaterIntake(9, 1680981488000), // April 8, 2023
                WaterIntake(10, 1680981488000), // April 8, 2023
                WaterIntake(11, 1680981488000), // April 8, 2023
                WaterIntake(12, 1681000000000), // April 9, 2023
                WaterIntake(13, 1681154288000), // April 10, 2023
                WaterIntake(14, 1681240688000), // April 11, 2023
            )
        runTest {
            list.forEach { entry ->
                testRepository.insertIntake2(entry.id!!, entry.timestamp!!)
            }
        }
    }

    @Test
    fun checkCount() =
        runTest {
            val count = testRepository.getCount()
            var found = 0
            count.collect { value -> found = value }
            println("count -> $found")
            assertEquals(14, found)
        }

    @Test
    fun removeLast() =
        runTest {
            testRepository.removeLastIntake()
            val count = testRepository.getCount()
            var found = 0
            count.collect { value -> found = value }
            assertEquals(13, found)
        }
}
