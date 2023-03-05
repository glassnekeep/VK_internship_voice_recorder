package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen

import androidx.recyclerview.widget.DiffUtil
import ru.glassnekeep.vk_internship_voice_recorder.data.Record

class RecordDiffUtilCallback(
    private val oldList: List<Pair<Record, Boolean>>,
    private val newList: List<Pair<Record, Boolean>>
): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].first.id == oldList[oldItemPosition].first.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
}