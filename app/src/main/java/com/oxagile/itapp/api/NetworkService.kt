package com.oxagile.itapp.api

import com.oxagile.itapp.model.Device
import com.oxagile.itapp.model.PasswordRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetworkService {

    @Headers("Accept: */*")
    @POST("devices/inform")
    fun sendDeviceData(@Body device: Device): Call<Device>

    @POST("devices/verify-password")
    suspend fun checkPassword(@Body password: PasswordRequest): String

    @GET("devices/require-update")
    suspend fun requireUpdate(): String

}