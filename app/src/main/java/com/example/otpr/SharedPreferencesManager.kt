package com.example.otpr

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(val context: Context) {

    companion object {
        private const val token = " "
        private const val state = "isLoggedIn"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(token, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(state, false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(state, loggedIn).apply()
    }
}
