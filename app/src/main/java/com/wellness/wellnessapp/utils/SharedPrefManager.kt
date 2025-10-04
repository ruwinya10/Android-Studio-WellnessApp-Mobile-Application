package com.wellness.wellnessapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellness.wellnessapp.models.Habit
import com.wellness.wellnessapp.models.MoodEntry

class SharedPrefManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val authManager = AuthManager(context)

    // Get current user's habits
    fun getHabits(): List<Habit> {
        val currentUser = authManager.getCurrentUser()
        return if (currentUser.isNotEmpty()) {
            getHabitsForUser(currentUser)
        } else {
            emptyList()
        }
    }

    // Save current user's habits
    fun saveHabits(habits: List<Habit>) {
        val currentUser = authManager.getCurrentUser()
        if (currentUser.isNotEmpty()) {
            saveHabitsForUser(currentUser, habits)
        }
    }

    // Get current user's mood entries
    fun getMoodEntries(): List<MoodEntry> {
        val currentUser = authManager.getCurrentUser()
        return if (currentUser.isNotEmpty()) {
            getMoodEntriesForUser(currentUser)
        } else {
            emptyList()
        }
    }

    // Save current user's mood entries
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val currentUser = authManager.getCurrentUser()
        if (currentUser.isNotEmpty()) {
            saveMoodEntriesForUser(currentUser, entries)
        }
    }

    // User-specific data methods
    private fun getHabitsForUser(username: String): List<Habit> {
        val json = sharedPref.getString("habits_$username", "[]") ?: "[]"
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveHabitsForUser(username: String, habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPref.edit().putString("habits_$username", json).apply()
    }

    private fun getMoodEntriesForUser(username: String): List<MoodEntry> {
        val json = sharedPref.getString("mood_entries_$username", "[]") ?: "[]"
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveMoodEntriesForUser(username: String, entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        sharedPref.edit().putString("mood_entries_$username", json).apply()
    }

    // Settings methods (can be global or user-specific)
    fun setHydrationInterval(minutes: Int) {
        val currentUser = authManager.getCurrentUser()
        val key = if (currentUser.isNotEmpty()) "hydration_interval_$currentUser" else "hydration_interval"
        sharedPref.edit().putInt(key, minutes).apply()
    }

    fun getHydrationInterval(): Int {
        val currentUser = authManager.getCurrentUser()
        val key = if (currentUser.isNotEmpty()) "hydration_interval_$currentUser" else "hydration_interval"
        return sharedPref.getInt(key, 60)
    }

    fun setRemindersEnabled(enabled: Boolean) {
        val currentUser = authManager.getCurrentUser()
        val key = if (currentUser.isNotEmpty()) "reminders_enabled_$currentUser" else "reminders_enabled"
        sharedPref.edit().putBoolean(key, enabled).apply()
    }

    fun areRemindersEnabled(): Boolean {
        val currentUser = authManager.getCurrentUser()
        val key = if (currentUser.isNotEmpty()) "reminders_enabled_$currentUser" else "reminders_enabled"
        return sharedPref.getBoolean(key, true)
    }
}