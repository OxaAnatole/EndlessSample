package com.ivanbakach.endlesssample.repository

import android.content.Context
import com.ivanbakach.endlesssample.api.RetrofitFactory
import com.ivanbakach.endlesssample.device.DeviceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class DeviceRepository {
    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun sendRepository(context: Context) = scope.launch(Dispatchers.IO) {
        DeviceUtil.generateDeviceInfo(context) {
            RetrofitFactory.apiService().sendDeviceData(it).execute()
        }
    }
}