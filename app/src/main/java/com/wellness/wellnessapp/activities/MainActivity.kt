package com.wellness.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.fragments.HabitFragment
import com.wellness.wellnessapp.fragments.HomeFragment
import com.wellness.wellnessapp.fragments.MoodFragment
import com.wellness.wellnessapp.fragments.SettingsFragment
import com.wellness.wellnessapp.fragments.StepCounterFragment
import com.wellness.wellnessapp.utils.AuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager(this)

        setupNavigation()
        loadInitialFragment()
    }

    private fun setupNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_habits -> {
                    replaceFragment(HabitFragment())
                    true
                }
                R.id.nav_mood -> {
                    replaceFragment(MoodFragment())
                    true
                }
                R.id.nav_steps -> {
                    replaceFragment(StepCounterFragment())
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadInitialFragment() {
        replaceFragment(HomeFragment())
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Navigation methods for home fragment buttons
    fun navigateToHabits() {
        replaceFragment(HabitFragment())
        bottomNavigation.selectedItemId = R.id.nav_habits
    }

    fun navigateToMood() {
        replaceFragment(MoodFragment())
        bottomNavigation.selectedItemId = R.id.nav_mood
    }

    fun navigateToSteps() {
        replaceFragment(StepCounterFragment())
        bottomNavigation.selectedItemId = R.id.nav_steps
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