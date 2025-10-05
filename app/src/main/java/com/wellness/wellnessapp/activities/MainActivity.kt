package com.wellness.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.fragments.HabitFragment
import com.wellness.wellnessapp.fragments.HomeFragment
import com.wellness.wellnessapp.fragments.MoodFragment
import com.wellness.wellnessapp.fragments.SettingsFragment
import com.wellness.wellnessapp.fragments.StepCounterFragment
import com.wellness.wellnessapp.utils.AuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager(this)

        setupNavigation()
        loadInitialFragment()
    }

    private fun setupNavigation() {
        val homeIcon: ImageView = findViewById(R.id.homeIcon)
        val habitsIcon: ImageView = findViewById(R.id.habitsIcon)
        val moodIcon: ImageView = findViewById(R.id.moodIcon)
        val stepsIcon: ImageView = findViewById(R.id.stepsIcon)
        val settingsIcon: ImageView = findViewById(R.id.settingsIcon)

        homeIcon.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        habitsIcon.setOnClickListener {
            replaceFragment(HabitFragment())
        }

        moodIcon.setOnClickListener {
            replaceFragment(MoodFragment())
        }

        stepsIcon.setOnClickListener {
            replaceFragment(StepCounterFragment())
        }

        settingsIcon.setOnClickListener {
            replaceFragment(SettingsFragment())
        }
    }

    private fun loadInitialFragment() {
        replaceFragment(HomeFragment())
    }

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

    fun navigateToSteps() {
        replaceFragment(StepCounterFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authManager.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}