package com.oxagile.itapp.api

import com.oxagile.itapp.device.Device
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetworkService {
    @Headers("Accept: */*")
    @POST("devices/inform")
    fun sendDeviceData(@Body device: Device): Call<Device>
}