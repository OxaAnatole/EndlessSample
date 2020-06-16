package com.oxagile.itapp.vm

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import java.io.File

class UpdateViewModel(application: Application): AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

}