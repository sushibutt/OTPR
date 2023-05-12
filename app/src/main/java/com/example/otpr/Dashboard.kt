package com.example.otpr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Dashboard : AppCompatActivity() {

    private val smsList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.toolbar, null)

        val about = v.findViewById<ImageView>(R.id.about_icon)


        // Check for internet connectivity
        fun isNetworkAvailable(): Boolean {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }


        // Display pop-up dialog
        fun showPopUp() {
            if (!isNetworkAvailable()) {
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.network_layout, null)

                val textViewPopupTitle = popupView.findViewById<TextView>(R.id.textViewPopupTitle)
                textViewPopupTitle.text = "No Internet Connection"

                val textViewPopupMessage =
                    popupView.findViewById<TextView>(R.id.textViewPopupMessage)
                textViewPopupMessage.text = "There was a problem connecting to the\n" +
                        "        server."

                val buttonPopupOk = popupView.findViewById<Button>(R.id.buttonPopupOk)
                buttonPopupOk.setOnClickListener {
                    finishAffinity()
                }

                val builder = AlertDialog.Builder(this)
                builder.setView(popupView)
                val alert = builder.create()
                alert.window?.setGravity(Gravity.CENTER)
                alert.show()
            }
        }

        showPopUp()

        about.setOnClickListener {
            about.alpha = 0.5f
            val intent = Intent(this, About_us::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        actionBar?.customView = v

        val smsListView = findViewById<ListView>(R.id.sms_list_view)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        //ListView adapter
        adapter = ArrayAdapter(this, R.layout.list_item, smsList)
        smsListView.adapter = adapter

        //Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            readSmsMessages()
            swipeRefreshLayout.isRefreshing = false
        }

        // Check for READ_SMS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1)
        } else {
            readSmsMessages()
        }

    }

    @SuppressLint("MissingInflatedId")
    fun readSmsMessages() {
        // Read SMS messages, Telephony Parameters
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            null,
            null,
            Telephony.Sms.DATE + " DESC LIMIT 5" // Add a limit
        )


        val smsListView = findViewById<ListView>(R.id.sms_list_view)

        cursor?.let {
            while (it.moveToNext()) {
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val formattedDate = SimpleDateFormat(
                    "MMMM dd, yyyy | HH:mm", Locale.getDefault()
                ).format(Date(date))

                // Inflate custom layout for each item
                val listItemView = layoutInflater.inflate(R.layout.list_item, null)
                val addressTextView = listItemView.findViewById<TextView>(R.id.address_textview)
                val dateTextView = listItemView.findViewById<TextView>(R.id.date_textview)
                val bodyTextView = listItemView.findViewById<TextView>(R.id.body_textview)

                // Set values for the TextViews
                addressTextView.text = address
                dateTextView.text = formattedDate
                bodyTextView.text = body

                // Add the custom layout view to the ListView
                smsListView.addHeaderView(listItemView)

                val jwtToken = SharedPreferencesManager(applicationContext).getToken()
                val consms = ("\n" + address + "\n" + formattedDate + "\n" + body)
                val sms = SMSData(consms)

                RetrofitClient.instance.sendSMS(jwtToken, sms).enqueue(object : Callback<SMSResponse> {
                    override fun onResponse(
                        call: Call<SMSResponse>, response: Response<SMSResponse>
                    ) {
                        if (response.body()?.success!!) {

                            //Flag message if sent
                            val sharedPreferences = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isMessageSent", true)
                            editor.apply()

                            Toast.makeText(
                                applicationContext, "Message Sent", Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                applicationContext, "Failed Sending Message", Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<SMSResponse>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "Failed Sending Message",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
            it.close()
            adapter.notifyDataSetChanged()
        }
    }
}