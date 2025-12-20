package com.romarickc.reminder.data.local.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.romarickc.reminder.commons.exportToFile
import com.romarickc.reminder.commons.getEntriesFromFile
import com.romarickc.reminder.domain.model.WaterIntake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM waterintake")
    fun getAllIntake(): Flow<List<WaterIntake>> // return a flow, we can call count and collect

    suspend fun getAllAndExportToFile(filePath: String): Int {
        // retrieve the data from the database
        val data = getAllIntake().firstOrNull() ?: return -1

        return exportToFile(filePath, data)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(data: List<WaterIntake>)

    // @Query("DELETE FROM waterintake")
    // fun deleteAll()
    fun importFromFile(filePath: String): Int {
        val data = getEntriesFromFile(filePath)

        if (data != null) {
            // deleteAll() // clear the existing data in the database
            insertAll(data) // insert the imported data back to the database
            Log.i("import", "done")
            return 0
        } else {
            return -1
        }
    }

    @Query("SELECT * FROM waterintake WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp")
    fun getPeriodWaterIntake(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WaterIntake>>

    @Query("SELECT COUNT(*) FROM waterintake")
    fun getCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM waterintake WHERE timestamp >= :timestamp")
    fun getCountTgtThis(timestamp: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntake(waterIntake: WaterIntake)

    @Query("DELETE FROM waterintake WHERE id = (SELECT MAX(id) FROM waterintake)")
    suspend fun removeLastIntake()

    // not useful for now
    @Query("SELECT * FROM waterintake WHERE id = :id")
    suspend fun getIntakeById(id: Long): WaterIntake

    @Update
    suspend fun updateIntake(waterIntake: WaterIntake)
}
