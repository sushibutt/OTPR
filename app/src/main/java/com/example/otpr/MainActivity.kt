package com.example.otpr

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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


        // Check for internet connectivity
        fun isNetworkAvailable(): Boolean {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        // Display pop-up dialog
        fun showPopUp() {
            if (!isNetworkAvailable()) {
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.network_layout, null)

                val textViewPopupTitle = popupView.findViewById<TextView>(R.id.textViewPopupTitle)
                textViewPopupTitle.text = "No Internet Connection"

                val textViewPopupMessage = popupView.findViewById<TextView>(R.id.textViewPopupMessage)
                textViewPopupMessage.text = "There was a problem connecting to the server."

                val buttonPopupOk = popupView.findViewById<Button>(R.id.buttonPopupOk)
                buttonPopupOk.setOnClickListener {
                    finishAffinity()
                }

                val builder = AlertDialog.Builder(this)
                builder.setView(popupView)
                val alert = builder.create()
                alert.show()
            }
        }

        showPopUp()


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
