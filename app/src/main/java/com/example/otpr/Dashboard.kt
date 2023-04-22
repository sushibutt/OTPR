package com.example.otpr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //Remember that user is logged in
        SharedPreferencesManager(this).setLoggedIn(true)


    }
}