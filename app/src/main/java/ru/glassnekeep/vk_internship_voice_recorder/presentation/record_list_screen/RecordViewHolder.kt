package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.recyclerview.widget.RecyclerView
import ru.glassnekeep.vk_internship_voice_recorder.R
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.databinding.RecordItemBinding
import java.util.concurrent.TimeUnit

class RecordViewHolder(
    private val binding: RecordItemBinding,
    private val itemClickListener: (Record) -> Unit
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Pair<Record, Boolean>) {
        with(binding) {
            val record = item.first
            val isPlaying = item.second
            recordName.text = record.name
            val time = record.time
            date.text = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time) % 60,
                TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60)
            val recordLength = record.length
            length.text = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(recordLength) % 60,
                TimeUnit.MILLISECONDS.toMinutes(recordLength) % 60,
                TimeUnit.MILLISECONDS.toSeconds(recordLength) % 60)
            play.setOnClickListener {
                itemClickListener(record)
                animate(isPlaying)
            }
        }
    }

    private fun animate(isPlaying: Boolean) {
        binding.play.apply {
            val animation = (drawable as AnimatedVectorDrawable)
            animation.start()
            handler.postDelayed({
                val resource = if (isPlaying) R.drawable.animated_play_to_pause else R.drawable.animated_pause_to_play
                setImageResource(resource)
                val background = if (isPlaying) R.drawable.circle_blue else R.drawable.circle_gray
                setBackgroundResource(background)
            }, 500L)
        }
    }
}