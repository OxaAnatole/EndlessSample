package com.oxagile.itapp.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Copyright (c) 2018 by Roman Sisik. All rights reserved.
 */
class DevAdminReceiver: DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d("DevAdminReceiver", "Device Owner Enabled")
    }
}