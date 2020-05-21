package com.ivanbakach.endlesssample

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ivanbakach.endlesssample.api.RetrofitFactory
import com.ivanbakach.endlesssample.device.DeviceUtil
import com.ivanbakach.endlesssample.endless.EndlessService
import com.ivanbakach.endlesssample.endless.ErrorReceiver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val apiList = mutableListOf<Any?>(
        "http://192.168.1.231:3001/", "http://10.168.31.231:3001/", "http://123.168.31.231:3001/")
    private val errorReceiver = ErrorReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DeviceUtil.generateDeviceInfo(this) {
            device_info.text = it.toString()
        }

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
        registerReceiver(errorReceiver, IntentFilter())
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(errorReceiver)
    }
}
