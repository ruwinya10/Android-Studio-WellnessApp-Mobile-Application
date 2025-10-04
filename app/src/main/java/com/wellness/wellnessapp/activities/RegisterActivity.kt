package com.wellness.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.utils.AuthManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authManager = AuthManager(this)
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        etConfirmPassword = findViewById(R.id.confirm_password)
        btnRegister = findViewById(R.id.register)
        tvLogin = findViewById(R.id.textView4)
        loading = findViewById(R.id.loading)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill all fields")
            return
        }

        if (username.length < 3) {
            showToast("Username must be at least 3 characters")
            return
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return
        }

        // Check for valid email format if using email
        if (username.contains("@") && !isValidEmail(username)) {
            showToast("Please enter a valid email address")
            return
        }

        showLoading(true)

        // Simulate registration process
        android.os.Handler().postDelayed({
            try {
                if (authManager.register(username, password)) {
                    showToast("Registration successful!")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    showToast("Username already exists")
                }
            } catch (e: Exception) {
                showToast("Registration failed: ${e.message}")
            } finally {
                showLoading(false)
            }
        }, 1500)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showLoading(show: Boolean) {
        loading.visibility = if (show) ProgressBar.VISIBLE else ProgressBar.GONE
        btnRegister.isEnabled = !show
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}