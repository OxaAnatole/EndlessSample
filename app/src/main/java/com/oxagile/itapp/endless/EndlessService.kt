package com.oxagile.itapp.endless

import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.oxagile.itapp.MainActivity
import com.oxagile.itapp.R
import com.oxagile.itapp.receiver.DownloadCompleteReceiver
import com.oxagile.itapp.repository.Repository
import com.oxagile.itapp.utils.UpdateUtils
import com.oxagile.itapp.api.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

class EndlessService : Service() {

    private val repository = Repository()
    private val handler = Handler()
    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
            repeatableAction()
            handler.postDelayed(this, TimeUnit.MINUTES.toMillis(1L))
        }
    }
    private lateinit var updateHelper: UpdateHelper

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        updateHelper.stop()
        handler.removeCallbacks(runnableCode)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateHelper = UpdateHelper(applicationContext, repository)
        updateHelper.start()
        startForeground()
        return START_STICKY
    }

    private fun startForeground() {
        handler.post(runnableCode)
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Endless service")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build()
        startForeground(SERVICE_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, "HEARTBEAT", importance)
            channel.description = "CHANEL DESCRIPTION"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun repeatableAction() {
        repository.sendDeviceInfo(this)
    }

    private class UpdateHelper(
        private val context: Context,
        private val repository: Repository
    ) {

        private val scope = repository.scope
        private val file: File = File(context.getExternalFilesDir(null)!!, "app.apk")
        private val period = TimeUnit.MINUTES.toMillis(10) //TODO
        private val receiver = DownloadCompleteReceiver { update() }
        private val handler = Handler()
        private val runnable = object : Runnable {
            override fun run() {
                requireUpdate() {
                    if (file.exists()) {
                        file.delete()
                    }
                    receiver.downloadId = UpdateUtils.download(context, file, URL)
                }
                handler.postDelayed(this, period)
            }
        }

        private inline fun requireUpdate(crossinline update: () -> Unit) = scope.launch(Dispatchers.IO) {
            when (val result = repository.requireUpdate()) {
                is Result.Success -> {
                    if (result.data) {
                        update()
                    }
                }
                is Result.Error -> {
                    Log.e(TAG, "requireUpdate", result.exception)
                }
            }
        }

        private fun update() {
            try {
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                if (dpm.isDeviceOwnerApp(context.packageName)) {
                    Log.d(TAG, "The app is device owner")
                    UpdateUtils.update(context, context.packageName, file.path)
                } else Log.d(TAG, "The app is not device owner")
            } catch (e: Exception) {
                Log.e(TAG, "Cannot update the app", e)
            }
        }

        fun start() {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            handler.post(runnable)
        }

        fun stop() {
            context.unregisterReceiver(receiver)
            handler.removeCallbacks(runnable)
        }

        companion object {
            private const val TAG = "UpdateHelper"
            private const val URL = "" //TODO
        }

    }

    companion object {
        const val STOP = "stop_service"
        private const val SERVICE_NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "ENDLESS"
        private const val REPEAT_INTERVAL: Long = 2000
    }
}