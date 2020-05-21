package com.ivanbakach.endlesssample.endless

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ErrorReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { Toast.makeText(it, "Sending is failed", Toast.LENGTH_SHORT).show() }
    }
}