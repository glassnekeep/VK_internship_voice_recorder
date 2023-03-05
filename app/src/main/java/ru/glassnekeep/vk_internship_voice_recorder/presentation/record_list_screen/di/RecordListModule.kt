package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.di.app.ViewModelKey
import ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.RecordAdapter
import ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.RecordListViewModel

@Module
interface RecordListModule {

    @Binds
    @IntoMap
    @ViewModelKey(RecordListViewModel::class)
    fun bindRecordListViewModel(viewModel: RecordListViewModel): ViewModel

    companion object {
        @Provides
        fun provideRecordAdapter(itemClickListener: (Record) -> Unit): RecordAdapter {
            return RecordAdapter(listOf(), itemClickListener)
        }
    }
}