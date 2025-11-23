package com.wellness.wellnessapp.models

import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),  //This creates a unique ID for each mood entry
    val moodEmoji: String,
    val moodText: String,
    val dateTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),  //stores the date and time when the entry is created in user’s local time
    val notes: String = ""
)