package com.romarickc.reminder.data.waterIntake_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.romarickc.reminder.domain.model.IntakeTarget
import kotlinx.coroutines.flow.Flow

@Dao
interface IntakeTargetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTarget(intakeTarget: IntakeTarget)

    @Query("SELECT currentTarget FROM intaketarget WHERE id = :id")
    fun getTarget(id:Int): Flow<Int>

    @Update
    suspend fun updateTarget(intakeTarget: IntakeTarget)
}