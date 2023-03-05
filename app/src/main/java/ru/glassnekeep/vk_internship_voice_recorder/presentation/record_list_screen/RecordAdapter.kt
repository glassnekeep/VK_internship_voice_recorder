package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.databinding.RecordItemBinding
import javax.inject.Inject

class RecordAdapter @Inject constructor(
    var recordList: List<Pair<Record, Boolean>>,
    private val itemClickListener: (Record) -> Unit
): RecyclerView.Adapter<RecordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder(
            RecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(recordList[position])
    }

    override fun getItemCount(): Int = recordList.size

}