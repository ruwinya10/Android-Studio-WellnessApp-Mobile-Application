package com.wellness.wellnessapp.models

import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val completed: Boolean = false,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)