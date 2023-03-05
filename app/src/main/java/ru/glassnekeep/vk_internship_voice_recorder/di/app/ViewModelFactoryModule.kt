package ru.glassnekeep.vk_internship_voice_recorder.di.app

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.glassnekeep.vk_internship_voice_recorder.presentation.ViewModelFactory

@Module
interface ViewModelFactoryModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}