package com.ivanbakach.endlesssample.endless

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ErrorReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            Toast.makeText(
                it,
                if (intent != null && intent.hasExtra(MESSAGE_KEY))
                    intent.getStringExtra(MESSAGE_KEY) else "Sending is failed",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        const val MESSAGE_KEY = "MESSAGE"
    }

}