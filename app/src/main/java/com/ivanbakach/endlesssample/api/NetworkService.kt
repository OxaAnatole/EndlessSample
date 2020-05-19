package com.ivanbakach.endlesssample.api

import com.ivanbakach.endlesssample.device.Device
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetworkService {
    @Headers("Accept: */*")
    @POST("devices/inform")
    fun sendDeviceData(@Body device: Device): Call<Device>
}