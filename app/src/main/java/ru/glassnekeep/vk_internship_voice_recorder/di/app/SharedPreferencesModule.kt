package ru.glassnekeep.vk_internship_voice_recorder.di.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
object SharedPreferencesModule {

    @Provides
    @AppScope
    fun provideSharedPreferences(@AppContext context: Context): SharedPreferences {
        return context.getSharedPreferences("record", Application.MODE_PRIVATE)
    }
}