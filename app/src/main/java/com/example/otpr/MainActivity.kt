package com.example.otpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.editToken)
        val button = findViewById<Button>(R.id.buttonEnter)

        button.setOnClickListener {

            val token = input.text.toString().trim()

            if (token.isEmpty()) {
                input.error = "Token is Required"
                input.requestFocus()
                return@setOnClickListener
            }

            RetrofitClient.instance.login(token)
                .enqueue(object : Callback<LogResponse> {
                    override fun onResponse(
                        call: Call<LogResponse>,
                        response: Response<LogResponse>
                    ) {
                        if (response.body()?.success!!) {

                            SharedPreferencesManager(applicationContext).setLoggedIn(true)
                            val intent = Intent(applicationContext, Dashboard::class.java)
                            startActivity(intent)

                            Log.e(TAG, "success")

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error Logging in",
                                Toast.LENGTH_LONG
                            ).show()

                            Log.e(TAG, "Fail")
                        }
                    }

                    override fun onFailure(call: Call<LogResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()

                        Log.e(TAG, "Fail")
                    }

                })
        }
    }


}
