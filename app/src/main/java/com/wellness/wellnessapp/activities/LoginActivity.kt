package com.wellness.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.utils.AuthManager

class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authManager = AuthManager(this)

        // Check if user is already logged in
        if (authManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        initViews()             // Initialize UI components
        setupClickListeners()   // Set button click actions
    }

    // Connect layout elements to Kotlin variables
    private fun initViews() {
        etUsername = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.login)
        tvRegister = findViewById(R.id.textView4)
        loading = findViewById(R.id.loading)
    }

    // Attach click listeners to buttons
    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Handles login logic with basic validation and simulated delay
    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        loading.visibility = ProgressBar.VISIBLE
        btnLogin.isEnabled = false

        // Simulate network login delay
        Handler().postDelayed({
            if (authManager.login(username, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
            loading.visibility = ProgressBar.GONE
            btnLogin.isEnabled = true
        }, 1000)
    }
}