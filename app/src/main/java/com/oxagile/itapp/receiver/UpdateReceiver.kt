package com.oxagile.itapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oxagile.itapp.MainActivity

class UpdateReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(i)
    }
}