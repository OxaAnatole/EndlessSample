package com.oxagile.itapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.oxagile.itapp.api.RetrofitFactory
import com.oxagile.itapp.device.DeviceUtil
import com.oxagile.itapp.endless.EndlessService
import com.oxagile.itapp.vm.UpdateViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val apiList = mutableListOf<Any?>(
        "http://192.168.1.231:3001/", "http://10.168.31.231:3001/", "http://123.168.31.231:3001/")
    private val viewModel: UpdateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DeviceUtil.generateDeviceInfo(this) {
            device_info.text = it.toString()
        }

        version_code.text = "Version code: ${BuildConfig.VERSION_CODE}"

        start.setOnClickListener {
            startService(Intent(this, EndlessService::class.java))
        }
        stop.setOnClickListener {
            val intent = Intent(this, EndlessService::class.java)
            stopService(intent)
        }

        val arrayAdapter =
            ArrayAdapter<Any?>(this, android.R.layout.simple_dropdown_item_1line, apiList)
        api_input.setAdapter(
            arrayAdapter
        )

        api_input.setText(RetrofitFactory.BASE_URL)
        api_change.setOnClickListener {
            val apiUrl = api_input.text
            RetrofitFactory.BASE_URL = apiUrl.toString()
            Toast.makeText(this, "Api URL was changed to $apiUrl", Toast.LENGTH_SHORT).show()
        }
        api_add.setOnClickListener {
            val apiUrl = api_input.text.toString()
            apiList.add(apiUrl)
            arrayAdapter.add(apiUrl)
            arrayAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Was added $apiUrl", Toast.LENGTH_SHORT).show()
        }
        button_update.setOnClickListener {
            try {
                viewModel.update(applicationContext, application.packageName, "/data/local/tmp/app-debug.apk")
            } catch (e: Exception) {
                Log.e(TAG, "Cannot update the app", e)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}