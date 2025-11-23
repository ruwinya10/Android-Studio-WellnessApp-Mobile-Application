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

    //Private helper to save a user’s habit list
    private fun saveHabitsForUser(username: String, habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPref.edit().putString("habits_$username", json).apply()
    }

    //Private helper to load a user’s mood entries
    private fun getMoodEntriesForUser(username: String): List<MoodEntry> {
        val json = sharedPref.getString("mood_entries_$username", "[]") ?: "[]"
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    //Private helper to save a user’s mood entries
    private fun saveMoodEntriesForUser(username: String, entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        sharedPref.edit().putString("mood_entries_$username", json).apply()
    }

    // Settings methods (can be global or user-specific)
    fun setHydrationInterval(minutes: Int) {
        sharedPref.edit().putInt("hydration_interval", minutes).apply()
    }

    //Function to retrieve the saved hydration interval
    fun getHydrationInterval(): Int {
        return sharedPref.getInt("hydration_interval", 60)
    }

    //Function to enable or disable hydration reminders
    fun setRemindersEnabled(enabled: Boolean) {
        sharedPref.edit().putBoolean("reminders_enabled", enabled).apply()
    }

    //Function to check if reminders are currently enabled
    fun areRemindersEnabled(): Boolean {
        return sharedPref.getBoolean("reminders_enabled", true)
    }

}