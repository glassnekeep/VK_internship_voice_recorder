package ru.glassnekeep.vk_internship_voice_recorder.di.app

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.glassnekeep.vk_internship_voice_recorder.data.recording.RecordServiceComponent
import ru.glassnekeep.vk_internship_voice_recorder.di.activity.ActivityComponent

@AppScope
@Component(modules = [SharedPreferencesModule::class, DatabaseModule::class, ViewModelFactoryModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance @AppContext context: Context): AppComponent
    }

    fun activityComponentFactory(): ActivityComponent.Factory

    fun recordServiceFactory(): RecordServiceComponent.Factory
}