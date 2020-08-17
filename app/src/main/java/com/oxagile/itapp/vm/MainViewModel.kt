package com.oxagile.itapp.vm

import androidx.lifecycle.ViewModel
import com.oxagile.itapp.network.NetworkFactory
import com.pixplicity.easyprefs.library.Prefs
import java.util.*

class MainViewModel: ViewModel() {

    private val apiList = Prefs.getStringSet(PREFS_URL_SET_KEY, HashSet<String>())

    init {
        addUrl(NetworkFactory.BASE_URL)
    }

    fun getUrlList(): List<String> = apiList.toList()

    fun setUrl(url: String) {
        addUrl(url)
        Prefs.putString(PREFS_URL_KEY, url)
    }

    private fun addUrl(url: String) {
        if (!apiList.contains(url)) {
            apiList.add(url)
            Prefs.putStringSet(PREFS_URL_SET_KEY, apiList)
        }
    }

    companion object {
        private const val PREFS_URL_SET_KEY = "url_set"
        const val PREFS_URL_KEY = "url"
    }

}