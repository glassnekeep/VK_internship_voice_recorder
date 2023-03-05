package ru.glassnekeep.vk_internship_voice_recorder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val length: Long,
    val time: Long,
    val filePath: String
)
