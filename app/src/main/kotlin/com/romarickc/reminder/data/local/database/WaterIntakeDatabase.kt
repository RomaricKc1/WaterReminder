package com.romarickc.reminder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.romarickc.reminder.data.local.dao.IntakeTargetDao
import com.romarickc.reminder.data.local.dao.PreferencesDao
import com.romarickc.reminder.data.local.dao.WaterIntakeDao
import com.romarickc.reminder.domain.model.IntakeTarget
import com.romarickc.reminder.domain.model.Preferences
import com.romarickc.reminder.domain.model.WaterIntake

@Database(
    entities = [WaterIntake::class, IntakeTarget::class, Preferences::class],
    version = 2,
    exportSchema = true,
)
abstract class WaterIntakeDatabase : RoomDatabase() {
    abstract val dao: WaterIntakeDao
    abstract val dao2: IntakeTargetDao
    abstract val dao3: PreferencesDao

    companion object {
        const val DATABASE_NAME = "waterintake-database.db"
    }
}
