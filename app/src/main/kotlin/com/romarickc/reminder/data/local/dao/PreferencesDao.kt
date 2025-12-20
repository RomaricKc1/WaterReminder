package com.romarickc.reminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.romarickc.reminder.domain.model.Preferences
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifPref(preferences: Preferences)

    @Query("SELECT notifLevel FROM preferences WHERE id = :id")
    fun getNotifPref(id: Int): Flow<Int>

    @Update
    suspend fun updateNotifPref(preferences: Preferences)
}
