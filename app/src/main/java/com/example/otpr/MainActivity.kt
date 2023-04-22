package com.example.otpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Check if user is logged in
        if (SharedPreferencesManager(this).isLoggedIn()) {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }



    }
}