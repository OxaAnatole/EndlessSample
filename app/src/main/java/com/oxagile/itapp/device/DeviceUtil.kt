package com.oxagile.itapp.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings


object DeviceUtil {
    fun generateDeviceInfo(context: Context, result: (Device) -> Unit) {
        result.invoke(
            Device(
                device_id = deviceId(context = context),
                brand = Build.MANUFACTURER,
                device_model = Build.MODEL,
                os_version = Build.VERSION.RELEASE
            )
        )
    }

    @SuppressLint("HardwareIds")
    private fun deviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}