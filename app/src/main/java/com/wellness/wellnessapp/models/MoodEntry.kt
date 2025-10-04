package com.wellness.wellnessapp.models

import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val moodEmoji: String,
    val moodText: String,
    val dateTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
    val notes: String = ""
)