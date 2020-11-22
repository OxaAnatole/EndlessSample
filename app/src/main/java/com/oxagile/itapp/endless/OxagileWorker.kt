package com.oxagile.itapp.endless

import android.app.DownloadManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.receiver.DownloadCompleteReceiver
import com.oxagile.itapp.repository.Repository
import com.oxagile.itapp.utils.UpdateUtils.download
import com.oxagile.itapp.utils.UpdateUtils.getFile
import com.oxagile.itapp.utils.UpdateUtils.update

const val FILE_NAME = "app.apk"

class OxagileWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    companion object {
        private val TAG = OxagileWorker::class.java.simpleName
    }

    private val file = context.getFile(FILE_NAME)
    private val receiver = DownloadCompleteReceiver { update() }
    private val repository = Repository()

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork")
        repository.sendDeviceInfo(context)
        requireUpdate {
            if (file.exists()) {
                file.delete()
            }
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            receiver.downloadId = context.download(file, NetworkFactory.getDownloadingUrl())
        }
        return Result.success()
    }

    private suspend inline fun requireUpdate(crossinline updateBlock: () -> Unit) {
        when (val result = repository.requireUpdate()) {
            is com.oxagile.itapp.network.Result.Success -> {
                if (result.data) {
                    updateBlock()
                }
            }
            is com.oxagile.itapp.network.Result.Error -> {
                Log.w(TAG, "requireUpdate", result.exception)
            }
        }
    }

    private fun update() {
        context.unregisterReceiver(receiver)
        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (dpm.isDeviceOwnerApp(context.packageName)) {
                Log.d(TAG, "The app is device owner")
                context.update(context.packageName, file.path)
            } else Log.e(TAG, "The app is not device owner")
        } catch (e: Exception) {
            Log.e(TAG, "Cannot update the app", e)
        }
    }

}