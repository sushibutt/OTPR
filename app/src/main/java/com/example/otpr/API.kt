package com.example.otpr

import retrofit2.Call
import retrofit2.http.*

interface API {
    @FormUrlEncoded
    @POST("authenticate")
    fun login(
        @Field("link_device_code") link_device_code: String
    ): Call<LogResponse>

    @FormUrlEncoded
    @POST("send/otp/to/telegram")
    fun sendSMS(
        @Field("message") message: SMSData
    ): Call<SMSResponse>
}
