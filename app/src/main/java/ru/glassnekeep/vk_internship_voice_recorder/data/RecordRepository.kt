package ru.glassnekeep.vk_internship_voice_recorder.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.glassnekeep.vk_internship_voice_recorder.data.database.RecordDao
import ru.glassnekeep.vk_internship_voice_recorder.di.app.AppScope
import javax.inject.Inject

@AppScope
class RecordRepository @Inject constructor(
    private val dao: RecordDao
) {
    fun getRecords() = dao.getRecords().flowOn(Dispatchers.IO)

    suspend fun getRecord(id: Long): Record = withContext(Dispatchers.IO) { dao.getRecord(id) }

    suspend fun insertRecord(record: Record) = withContext(Dispatchers.IO) { dao.insertRecord(record) }

    suspend fun updateRecord(record: Record) = withContext(Dispatchers.IO) { dao.updateRecord(record) }

    suspend fun deleteRecord(record: Record) = withContext(Dispatchers.IO) { dao.deleteRecord(record) }

    fun countRecords() = dao.countRecords()
}