package com.wellness.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.utils.AuthManager

class SplashActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authManager = AuthManager(this)

        val logo: ImageView = findViewById(R.id.logo)
        logo.setOnClickListener {
            if (authManager.isLoggedIn()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }
}