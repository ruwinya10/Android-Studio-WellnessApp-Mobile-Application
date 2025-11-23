package com.wellness.wellnessapp.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.fragments.HabitFragment
import com.wellness.wellnessapp.fragments.HomeFragment
import com.wellness.wellnessapp.fragments.MoodFragment
import com.wellness.wellnessapp.fragments.SettingsFragment
import com.wellness.wellnessapp.utils.AuthManager
import com.google.android.material.navigation.NavigationView
import com.wellness.wellnessapp.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager(this)  // Initialize authentication manager

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        setupDrawer()
        setupNavigation()
        loadInitialFragment()
        createNotificationChannel()

        // Check for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Ask for notification permission if not already granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

    }

    // Setup bottom navigation icons (Home, Habits, Mood, Settings, Profile)
    private fun setupNavigation() {
        val homeIcon: ImageView = findViewById(R.id.homeIcon)
        val habitsIcon: ImageView = findViewById(R.id.habitsIcon)
        val moodIcon: ImageView = findViewById(R.id.moodIcon)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)
        val profileIcon: ImageView = findViewById(R.id.profileIcon)

        homeIcon.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        habitsIcon.setOnClickListener {
            replaceFragment(HabitFragment())
        }

        moodIcon.setOnClickListener {
            replaceFragment(MoodFragment())
        }

        settingsIcon.setOnClickListener {
            replaceFragment(SettingsFragment())
        }

        profileIcon.setOnClickListener {
            replaceFragment(ProfileFragment())
        }
    }

    // Loads the default fragment when the app starts
    private fun loadInitialFragment() {
        replaceFragment(HomeFragment())
    }

    // Helper function to switch fragments dynamically
    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Navigation methods for home fragment buttons
    fun navigateToHabits() {
        replaceFragment(HabitFragment())
    }

    fun navigateToMood() {
        replaceFragment(MoodFragment())
    }

    fun navigateToSettings() {
        replaceFragment(SettingsFragment())
    }

    // Setup side navigation drawer
    private fun setupDrawer() {
        val header = navigationView.getHeaderView(0)
        val greetingText: android.widget.TextView = header.findViewById(R.id.nav_greeting)
        val usernameText: android.widget.TextView = header.findViewById(R.id.nav_username)
        val currentUser = authManager.getCurrentUser().ifBlank { "User" }  // Fetch logged-in username
        greetingText.text = "Hello!"
        usernameText.text = currentUser

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    authManager.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Function to open drawer programmatically (if needed)
    fun openDrawer() {
        drawerLayout.open()
    }

    // Create a notification channel for Android O and above
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hydration_reminder",
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you to drink water"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

}