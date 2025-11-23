package com.wellness.wellnessapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.wellness.wellnessapp.models.Habit
import com.wellness.wellnessapp.models.MoodEntry

class AuthManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Returns true if registration succeeds, false if the user already exists
    fun register(username: String, password: String): Boolean {
        // Check if username already exists
        if (sharedPref.contains("user_$username")) {
            return false
        }

        // Save user credentials
        sharedPref.edit().putString("user_$username", password).apply()

        // Initialize empty data for new user
        saveHabitsForUser(username, emptyList())
        saveMoodEntriesForUser(username, emptyList())

        return true
    }

    // Function to log a user in using a username and password
    fun login(username: String, password: String): Boolean {
        val savedPassword = sharedPref.getString("user_$username", null)
        return if (savedPassword == password) {
            // Save login state
            sharedPref.edit().putBoolean("isLoggedIn", true).apply()
            sharedPref.edit().putString("currentUser", username).apply()
            true
        } else {
            false
        }
    }

    //check if a user is currently logged in
    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("isLoggedIn", false)
    }

    //get the username of the currently logged-in user
    fun getCurrentUser(): String {
        return sharedPref.getString("currentUser", "") ?: ""
    }

    //log the user out of the app
    fun logout() {
        sharedPref.edit().putBoolean("isLoggedIn", false).apply()
        sharedPref.edit().remove("currentUser").apply()
    }

    // Function to save a given user's list of habits
    fun saveHabitsForUser(username: String, habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPref.edit().putString("habits_$username", json).apply()
    }

    fun saveMoodEntriesForUser(username: String, entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        sharedPref.edit().putString("mood_entries_$username", json).apply()
    }

}