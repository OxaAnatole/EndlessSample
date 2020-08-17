package com.oxagile.itapp.endless

import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.oxagile.itapp.ui.activity.MainActivity
import com.oxagile.itapp.R
import com.oxagile.itapp.receiver.DownloadCompleteReceiver
import com.oxagile.itapp.repository.Repository
import com.oxagile.itapp.utils.UpdateUtils
import com.oxagile.itapp.network.Result
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.ui.fragment.PERIOD_DEFAULT
import com.oxagile.itapp.ui.fragment.PREFS_PERIOD_KEY
import com.pixplicity.easyprefs.library.Prefs
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
    private val binder = ServiceBinder()

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        updateHelper.stop()
        handler.removeCallbacks(runnableCode)
        binder.serviceStarted = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateHelper = UpdateHelper(applicationContext, repository)
        updateHelper.start()
        startForeground()
        binder.serviceStarted = true
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
                .setContentTitle("IT Oxagile app")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build()
        startForeground(SERVICE_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, "Oxagile IT service", importance)
            channel.description = "The channel is used for the service"
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
        private val receiver = DownloadCompleteReceiver { update() }
        private val handler = Handler()
        private val runnable = object : Runnable {
            override fun run() {
                requireUpdate() {
                    if (file.exists()) {
                        file.delete()
                    }
                    receiver.downloadId = UpdateUtils.download(context, file, NetworkFactory.getDownloadingUrl())
                }
                val period = TimeUnit.MINUTES.toMillis(Prefs.getInt(PREFS_PERIOD_KEY, PERIOD_DEFAULT).toLong())
                handler.postDelayed(this, period)
            }
        }

        private inline fun requireUpdate(crossinline updateBlock: () -> Unit) = scope.launch(Dispatchers.IO) {
            when (val result = repository.requireUpdate()) {
                is Result.Success -> {
                    if (result.data) {
                        updateBlock()
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
                } else Log.e(TAG, "The app is not device owner")
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
        }

    }

    inner class ServiceBinder: Binder() {
        var serviceStarted = false
    }

    companion object {
        private const val SERVICE_NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "OXAGILE"
    }
}