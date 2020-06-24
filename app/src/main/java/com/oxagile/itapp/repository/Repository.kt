package com.oxagile.itapp.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.utils.DeviceUtil
import com.oxagile.itapp.endless.ErrorReceiver
import com.oxagile.itapp.model.DevicePassword
import com.oxagile.itapp.model.PasswordRequest
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine
import com.oxagile.itapp.network.Result
import java.lang.IllegalStateException
import kotlin.coroutines.resume

class Repository {
    private val TAG = "DeviceRepository"
    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    val scope = CoroutineScope(coroutineContext)

    fun sendDeviceInfo(context: Context) = scope.launch(Dispatchers.IO) {
        DeviceUtil.generateDeviceInfo(context) {
            try {
                NetworkFactory.apiService().sendDeviceData(it).execute()
            } catch (e: Exception) {
                Log.w(TAG, "Sending is failed", e)
                val intent = Intent(context, ErrorReceiver::class.java)
                intent.putExtra(ErrorReceiver.MESSAGE_KEY, e.message)
                context.sendBroadcast(intent)
            }
        }
    }

    suspend fun checkPassword(password: String): Result<Boolean> {
        return try {
            val request = PasswordRequest(DevicePassword(password))
            val answer = NetworkFactory.apiService().checkPassword(request)
            processAnswer(answer)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun requireUpdate(): Result<Boolean> {
        return try {
            val answer = NetworkFactory.apiService().requireUpdate()
            processAnswer(answer)
        } catch(e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun processAnswer(answer: String) = suspendCoroutine<Result<Boolean>> {
        when (answer) {
            "0" -> it.resume(Result.Success(false))
            "1" -> it.resume(Result.Success(true))
            else -> it.resume(Result.Error(IllegalStateException(answer)))
        }
    }

}