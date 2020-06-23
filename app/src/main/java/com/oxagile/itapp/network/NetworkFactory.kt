package com.oxagile.itapp.network

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.oxagile.itapp.ItApp.Companion.networkFlipperPlugin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkFactory {

    const val INSTALLING_FILE = "app.apk"
    var BASE_URL = "http://192.168.31.231:3001/"

    fun apiService(): NetworkService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(NetworkService::class.java)
    }

    fun getDownloadingUrl() = "${BASE_URL}app/${INSTALLING_FILE}"

}
