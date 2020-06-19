package com.oxagile.itapp.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadCompleteReceiver(private val block: () -> Unit): BroadcastReceiver() {

    var downloadId: Long = -1

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        intent?.let {
            val id: Long = it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d(TAG, "Download ID = $id")
            if (downloadId == id) {
                block()
            }
        }
    }

    companion object {
        private const val TAG = "DownloadCompleteRec-r"
    }

}