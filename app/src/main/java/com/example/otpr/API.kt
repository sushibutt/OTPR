package com.example.otpr

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {
    @FormUrlEncoded
    @POST("authenticate")
    fun login(
        @Field("link_device_code") link_device_code: String
    ): Call<LogResponse>

    @POST("messages")
    fun sendSMS(
        @Field("messages") messages: String
    ): Call<SMSResponse>
}