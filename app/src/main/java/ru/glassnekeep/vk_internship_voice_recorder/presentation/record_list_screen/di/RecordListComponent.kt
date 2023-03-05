package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.di

import android.content.Context
import dagger.BindsInstance
import dagger.Subcomponent
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.RecordListFragment

@Subcomponent(modules = [RecordListModule::class])
interface RecordListComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance @RecordListFragmentContext context: Context, @BindsInstance itemClickListener: (Record) -> Unit): RecordListComponent
    }

    fun inject(fragment: RecordListFragment)
}