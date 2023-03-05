package ru.glassnekeep.vk_internship_voice_recorder

import android.app.Application
import ru.glassnekeep.vk_internship_voice_recorder.di.app.AppComponent
import ru.glassnekeep.vk_internship_voice_recorder.di.app.DaggerAppComponent

class RecordApplication: Application() {
    val appComponent: AppComponent by lazy {
        initAppComponent()
    }

    private fun initAppComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}