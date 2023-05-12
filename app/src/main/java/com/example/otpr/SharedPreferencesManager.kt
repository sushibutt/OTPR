package com.example.otpr

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(val context: Context) {

    companion object {
        private const val name = "my_app_preferences"
        private const val jwt_token = "token"
        private const val state = "isLoggedIn"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(jwt_token, Context.MODE_PRIVATE)
    }

    fun setToken(token: String?) {
        sharedPreferences.edit().putString(jwt_token, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(jwt_token, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(state, false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(state, loggedIn).apply()
    }
}
