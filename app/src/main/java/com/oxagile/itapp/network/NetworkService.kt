package com.oxagile.itapp.network

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

    @Headers("Accept: */*")
    @POST("devices/verify-password")
    suspend fun checkPassword(@Body password: PasswordRequest): String

    @Headers("Accept: */*")
    @GET("devices/require-update")
    suspend fun requireUpdate(): String

}