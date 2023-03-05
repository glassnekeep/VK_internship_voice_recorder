package ru.glassnekeep.vk_internship_voice_recorder.di.activity

import dagger.Subcomponent
import ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.di.RecordListComponent

@ActivityScope
@Subcomponent
interface ActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun recordListComponentFactory(): RecordListComponent.Factory
}