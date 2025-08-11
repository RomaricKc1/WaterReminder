package com.romarickc.reminder.data.waterIntakeDatabase

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.romarickc.reminder.domain.model.WaterIntake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM waterintake")
    fun getAllIntake(): Flow<List<WaterIntake>> // return a flow, we can call count and collect

    suspend fun getAllAndExportToFile(filePath: String): Int {
        val data = getAllIntake().firstOrNull() ?: return -1 // retrieve the data from the database

        /*val documentsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDirectory, filePath)*/
        val file = File(filePath)

        try {
            val fileOutputStream =
                withContext(Dispatchers.IO) {
                    FileOutputStream(file)
                }
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            val bufferedWriter = BufferedWriter(outputStreamWriter)

            for (item in data) {
                withContext(Dispatchers.IO) {
                    bufferedWriter.write("${item.id},${item.timestamp}")
                }
                withContext(Dispatchers.IO) {
                    bufferedWriter.newLine()
                }
            }

            withContext(Dispatchers.IO) {
                bufferedWriter.flush()
            }
            withContext(Dispatchers.IO) {
                bufferedWriter.close()
            }
            Log.i("export", "done")
            return 0
        } catch (e: IOException) {
            // e.printStackTrace()
            Log.i("export-e", "$e")
            return -1
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(data: List<WaterIntake>)

    // @Query("DELETE FROM waterintake")
    // fun deleteAll()
    fun importFromFile(filePath: String): Int {
        try {
            // val documentsDirectory =
            //    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(filePath)
            val lines = file.readLines()

            val data =
                lines.map { line ->
                    val (_, timestamp) = line.split(",") // file format is "id,timestamp"
                    WaterIntake(null, timestamp.toLongOrNull())
                }

            // deleteAll() // clear the existing data in the database
            insertAll(data) // insert the imported data back to the database
            Log.i("import", "done")
            return 0
        } catch (e: Exception) {
            Log.e("import-e", "$e")
            // e.printStackTrace()
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
