package com.oxagile.itapp.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.oxagile.itapp.endless.OxagileWorker
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.prefs.PrefsConstants.PERIOD_MINUTES_KEY
import com.oxagile.itapp.prefs.PrefsConstants.PERIOD_MINUTES_VALUE
import com.oxagile.itapp.prefs.PrefsConstants.URL_KEY
import com.oxagile.itapp.prefs.PrefsConstants.URL_SET_KEY
import com.pixplicity.easyprefs.library.Prefs
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    private val apiList = Prefs.getStringSet(URL_SET_KEY, HashSet())

    init {
        addUrl(NetworkFactory.BASE_URL)
    }

    fun startService() {
        val interval = Prefs.getInt(PERIOD_MINUTES_KEY, PERIOD_MINUTES_VALUE).toLong()
        val worker =
            PeriodicWorkRequest.Builder(OxagileWorker::class.java, interval, TimeUnit.MINUTES)
                .addTag(WORK_TAG)
                .build()
        WorkManager.getInstance(app).enqueue(worker)
    }

    fun stopService() {
        WorkManager.getInstance(app).cancelAllWorkByTag(WORK_TAG)
    }

    fun getUrlList(): List<String> = apiList.toList()

    fun setUrl(url: String) {
        addUrl(url)
        Prefs.putString(URL_KEY, url)
    }

    private fun addUrl(url: String) {
        if (!apiList.contains(url)) {
            apiList.add(url)
            Prefs.putStringSet(URL_SET_KEY, apiList)
        }
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
        const val WORK_TAG = "work"
    }

}