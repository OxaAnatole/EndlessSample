package com.ivanbakach.endlesssample.endless

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.ivanbakach.endlesssample.MainActivity
import com.ivanbakach.endlesssample.R
import com.ivanbakach.endlesssample.api.RetrofitFactory.apiService
import com.ivanbakach.endlesssample.device.DeviceUtil.generateDeviceInfo
import com.ivanbakach.endlesssample.repository.DeviceRepository
import java.util.concurrent.TimeUnit

class EndlessService : Service() {

    private val handler = Handler()
    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
            repeatableAction()
            handler.postDelayed(this, TimeUnit.MINUTES.toMillis(1L))
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableCode)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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
        DeviceRepository().sendRepository(this)
    }

    companion object {
        const val STOP = "stop_service"
        private const val SERVICE_NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "ENDLESS"
        private const val REPEAT_INTERVAL: Long = 2000
    }
}