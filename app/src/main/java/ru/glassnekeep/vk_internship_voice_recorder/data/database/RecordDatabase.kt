package ru.glassnekeep.vk_internship_voice_recorder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.glassnekeep.vk_internship_voice_recorder.data.Record

@Database(entities = [Record::class], version = 1, )
abstract class RecordDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: RecordDatabase? = null
        fun getInstance(context: Context): RecordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RecordDatabase::class.java,
                    "record_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}