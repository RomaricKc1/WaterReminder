package com.romarickc.reminder.data.repository

import com.romarickc.reminder.commons.Constants.DB_NOTIF_LEVEL_IDX
import com.romarickc.reminder.data.local.dao.IntakeTargetDao
import com.romarickc.reminder.data.local.dao.PreferencesDao
import com.romarickc.reminder.data.local.dao.WaterIntakeDao
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

    @Deprecated("Using file system. I don't like it anymore. Use http server comm")
    override fun importFromFile(filePath: String) = dao.importFromFile(filePath)

    override fun importFromStr(stream: String): Int? = dao.importFromStr(stream)

    @Deprecated("Using file system. I don't like it anymore. Use http server comm")
    override suspend fun getAllAndExportToFile(filePath: String) = dao.getAllAndExportToFile(filePath)

    override suspend fun getAllToStr(): String? = dao.getAllToStr()

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
        val intakeTarget =
            IntakeTarget(
                id = 1,
                currentTarget = target,
            )
        dao2.insertTarget(intakeTarget)
    }

    override fun getTarget(id: Int): Flow<Int> = dao2.getTarget(id)

    override suspend fun updateTarget(intakeTarget: IntakeTarget) {
        dao2.updateTarget(intakeTarget)
    }

    // preferences notifs
    override suspend fun insertNotifPref(level: Int) {
        val preferences =
            Preferences(
                id = DB_NOTIF_LEVEL_IDX,
                notifLevel = level,
            )
        dao3.insertNotifPref(preferences)
    }

    override fun getNotifPref(id: Int): Flow<Int> = dao3.getNotifPref(id)

    override suspend fun updateNotifPref(preferences: Preferences) {
        dao3.updateNotifPref(preferences)
    }
}
