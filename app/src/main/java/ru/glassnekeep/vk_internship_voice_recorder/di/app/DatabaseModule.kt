package ru.glassnekeep.vk_internship_voice_recorder.di.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.glassnekeep.vk_internship_voice_recorder.data.database.RecordDao
import ru.glassnekeep.vk_internship_voice_recorder.data.database.RecordDatabase

@Module
object DatabaseModule {
    @Provides
    @AppScope
    fun provideDatabase(@AppContext context: Context): RecordDatabase {
        return Room.databaseBuilder(
            context,
            RecordDatabase::class.java,
            "record_database"
        ).build()
    }

    @Provides
    @AppScope
    fun provideDao(database: RecordDatabase): RecordDao {
        return database.recordDao()
    }
}