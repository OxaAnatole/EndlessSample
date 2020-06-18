package com.oxagile.itapp.vm

import android.app.Application
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import java.io.File

class UpdateViewModel(application: Application): AndroidViewModel(application) {
    
    fun download(context: Context) {
        val file = File(context.getExternalFilesDir(null), "install") //TODO
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle("APK-file") // Title of the Download Notification
                .setDescription("Downloading") // Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN) // Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file)) // Uri of the destination file
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false) // Set if charging is required to begin the download
        }
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(Exception::class)
    fun update(context: Context, packageName: String, apkPath: String) {
        // PackageManager provides an instance of PackageInstaller
        val packageInstaller = context.packageManager.packageInstaller
        // Prepare params for installing one APK file with MODE_FULL_INSTALL
        // We could use MODE_INHERIT_EXISTING to install multiple split APKs
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        params.setAppPackageName(packageName)
        // Get a PackageInstaller.Session for performing the actual update
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)
        // Copy APK file bytes into OutputStream provided by install Session
        val out = session.openWrite(packageName, 0, -1)
        val fis = File(apkPath).inputStream()
        fis.copyTo(out)
        session.fsync(out)
        out.close()
        // The app gets killed after installation session commit
        session.commit(
            PendingIntent.getBroadcast(context, sessionId, Intent("android.intent.action.MAIN"), 0)
                .intentSender
        )
    }

    companion object {
        private const val URL = "http://speedtest.ftp.otenet.gr/files/test10Mb.db"
    }

}