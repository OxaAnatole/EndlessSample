package com.ivanbakach.endlesssample.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ivanbakach.endlesssample.api.RetrofitFactory
import com.ivanbakach.endlesssample.device.DeviceUtil
import com.ivanbakach.endlesssample.endless.ErrorReceiver
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class DeviceRepository {
    private val TAG = "DeviceRepository"
    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun sendRepository(context: Context) = scope.launch(Dispatchers.IO) {
        DeviceUtil.generateDeviceInfo(context) {
            try {
                RetrofitFactory.apiService().sendDeviceData(it).execute()
            } catch (e: Exception) {
                Log.w(TAG, "Sending is failed", e)
                context.sendBroadcast(Intent(context, ErrorReceiver::class.java))
            }
        }
    }
}