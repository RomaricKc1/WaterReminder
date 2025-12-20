package com.romarickc.reminder.base.data.repository

import com.romarickc.reminder.domain.model.IntakeTarget
import com.romarickc.reminder.domain.model.Preferences
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRepository : WaterIntakeRepository {
    private val intakes = mutableListOf<WaterIntake>()
    private val targetList = mutableListOf<IntakeTarget>()
    private val prefList = mutableListOf<Preferences>()

    override fun getAllIntake(): Flow<List<WaterIntake>> = flow { intakes }

    override fun getCount(): Flow<Int> = flow { emit(intakes.size) }

    override suspend fun getIntakeById(id: Long): WaterIntake = intakes.find { it.id == id }!!

    fun insertIntake2(
        id: Long,
        ts: Long,
    ) {
        intakes.add(WaterIntake(id, ts))
    }

    override suspend fun updateIntake(waterIntake: WaterIntake) {
        val idx: Int = waterIntake.id?.toInt()!!
        intakes[idx] = waterIntake
    }

    override suspend fun removeLastIntake() {
        intakes.removeLast()
    }

    override suspend fun insertTarget(target: Int) {
        targetList.add(0, IntakeTarget(target))
    }

    override fun getTarget(id: Int): Flow<Int> = flow { targetList.elementAt(0) }

    override suspend fun updateTarget(intakeTarget: IntakeTarget) {
        targetList.add(0, intakeTarget)
    }

    override suspend fun insertNotifPref(level: Int) {
        prefList.add(0, Preferences(level))
    }

    override fun getNotifPref(id: Int): Flow<Int> = flow { prefList.elementAt(0) }

    override suspend fun updateNotifPref(preferences: Preferences) {
        prefList.add(0, preferences)
    }

    override fun getCountTgtThis(timestamp: Long): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun getPeriodWaterIntake(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WaterIntake>> {
        TODO("Not yet implemented")
    }

    override fun importFromFile(filePath: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAndExportToFile(filePath: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertIntake() {
        TODO("Not yet implemented")
    }
}
