package com.example.otpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView


class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        if (SharedPreferencesManager(this).isLoggedIn()) {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        val splash_logo = findViewById<ImageView>(R.id.splash_logo)

        splash_logo.alpha = 0f
        splash_logo.animate().setDuration(1500).alpha(1f).withEndAction {
            val sharedPreferencesManager = SharedPreferencesManager(this)
            // Check if user is already logged in
            if (sharedPreferencesManager.isLoggedIn()) {
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()
            } else {
                // User is not logged in, show login screen
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}