package ru.glassnekeep.vk_internship_voice_recorder.data.recording

import dagger.Subcomponent

@Subcomponent
interface RecordServiceComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): RecordServiceComponent
    }

    fun inject(service: RecordService)
}