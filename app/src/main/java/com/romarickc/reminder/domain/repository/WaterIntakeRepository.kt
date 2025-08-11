package com.romarickc.reminder.domain.repository

import com.romarickc.reminder.domain.model.IntakeTarget
import com.romarickc.reminder.domain.model.Preferences
import com.romarickc.reminder.domain.model.WaterIntake
import kotlinx.coroutines.flow.Flow

interface WaterIntakeRepository {
    // intake
    fun getAllIntake(): Flow<List<WaterIntake>>

    fun getPeriodWaterIntake(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WaterIntake>>

    fun importFromFile(filePath: String): Int

    suspend fun getAllAndExportToFile(filePath: String): Int

    fun getCount(): Flow<Int>

    fun getCountTgtThis(timestamp: Long): Flow<Int>

    suspend fun getIntakeById(id: Long): WaterIntake

    suspend fun insertIntake()

    suspend fun updateIntake(waterIntake: WaterIntake)

    suspend fun removeLastIntake()

    // target
    suspend fun insertTarget(target: Int)

    fun getTarget(id: Int): Flow<Int>

    suspend fun updateTarget(intakeTarget: IntakeTarget)

    // preferences
    suspend fun insertNotifPref(level: Int)

    fun getNotifPref(id: Int): Flow<Int>

    suspend fun updateNotifPref(preferences: Preferences)
}
