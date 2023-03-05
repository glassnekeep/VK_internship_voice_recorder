package ru.glassnekeep.vk_internship_voice_recorder.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.glassnekeep.vk_internship_voice_recorder.data.Record

@Dao
interface RecordDao {

    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecord(id: Long): Record

    @Query("SELECT * FROM recordings")
    fun getRecords(): Flow<List<Record>>

    @Insert
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("SELECT COUNT(*) FROM recordings")
    fun countRecords(): Int
}