package com.romarickc.reminder.data.repository

import com.romarickc.reminder.data.waterIntakeDatabase.IntakeTargetDao
import com.romarickc.reminder.data.waterIntakeDatabase.PreferencesDao
import com.romarickc.reminder.data.waterIntakeDatabase.WaterIntakeDao
import com.romarickc.reminder.domain.model.IntakeTarget
import com.romarickc.reminder.domain.model.Preferences
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class WaterIntakeRepositoryImpl(
    private val dao: WaterIntakeDao,
    private val dao2: IntakeTargetDao,
    private val dao3: PreferencesDao,
) : WaterIntakeRepository {
    override fun getAllIntake(): Flow<List<WaterIntake>> = dao.getAllIntake()

    override fun getPeriodWaterIntake(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WaterIntake>> = dao.getPeriodWaterIntake(startTimestamp, endTimestamp)

    override fun importFromFile(filePath: String) = dao.importFromFile(filePath)

    override suspend fun getAllAndExportToFile(filePath: String) = dao.getAllAndExportToFile(filePath)

    override fun getCount(): Flow<Int> = dao.getCount()

    override fun getCountTgtThis(timestamp: Long): Flow<Int> = dao.getCountTgtThis(timestamp)

    override suspend fun getIntakeById(id: Long) = dao.getIntakeById(id)

    override suspend fun insertIntake() {
        val currentTime = Instant.now().toEpochMilli()
        val intake =
            WaterIntake(
                timestamp = currentTime,
            )
        dao.insertIntake(waterIntake = intake)
    }

    override suspend fun removeLastIntake() {
        dao.removeLastIntake()
    }

    override suspend fun updateIntake(waterIntake: WaterIntake) {
        dao.updateIntake(waterIntake)
    }

    // new dao2
    override suspend fun insertTarget(target: Int) {
        val intarget =
            IntakeTarget(
                id = 1,
                currentTarget = target,
            )
        dao2.insertTarget(intarget)
    }

    override fun getTarget(id: Int): Flow<Int> = dao2.getTarget(id)

    override suspend fun updateTarget(intakeTarget: IntakeTarget) {
        dao2.updateTarget(intakeTarget)
    }

    // preferences
    override suspend fun insertNotifPref(level: Int) {
        val preferences =
            Preferences(
                id = 1,
                notifLevel = level,
            )
        dao3.insertNotifPref(preferences)
    }

    override fun getNotifPref(id: Int): Flow<Int> = dao3.getNotifPref(id)

    override suspend fun updateNotifPref(preferences: Preferences) {
        dao3.updateNotifPref(preferences)
    }
}
