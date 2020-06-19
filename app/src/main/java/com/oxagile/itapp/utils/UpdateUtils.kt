package com.oxagile.itapp.utils

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File

object UpdateUtils {

    fun download(context: Context, dir: File, url: String): Long {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle("Oxagile IT app updating") // Title of the Download Notification
                .setDescription("Downloaded") // Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                // Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(dir)) // Uri of the destination file
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false) // Set if charging is required to begin the download
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManager.enqueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(Exception::class)
    fun update(context: Context, packageName: String, path: String) {
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
        val fis = File(path).inputStream()
        fis.copyTo(out)
        session.fsync(out)
        out.close()
        // The app gets killed after installation session commit
        session.commit(
            PendingIntent.getBroadcast(context, sessionId, Intent("android.intent.action.MAIN"), 0)
                .intentSender
        )
    }

}