package com.oxagile.itapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.oxagile.itapp.BuildConfig
import com.oxagile.itapp.R
import com.oxagile.itapp.api.RetrofitFactory
import com.oxagile.itapp.endless.EndlessService
import com.oxagile.itapp.utils.DeviceUtil
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val apiList = mutableListOf<Any?>(
        "http://192.168.1.231:3001/", "http://10.168.31.231:3001/", "http://123.168.31.231:3001/")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = view.context

        DeviceUtil.generateDeviceInfo(context) {
            device_info.text = it.toString()
        }

        version_code.text = "Version code: ${BuildConfig.VERSION_CODE}"

        start.setOnClickListener {
            context.startService(Intent(context, EndlessService::class.java))
        }
        stop.setOnClickListener {
            val intent = Intent(context, EndlessService::class.java)
            context.stopService(intent)
        }

        val arrayAdapter =
            ArrayAdapter<Any?>(context, android.R.layout.simple_dropdown_item_1line, apiList)
        api_input.setAdapter(
            arrayAdapter
        )

        api_input.setText(RetrofitFactory.BASE_URL)
        api_change.setOnClickListener {
            val apiUrl = api_input.text
            RetrofitFactory.BASE_URL = apiUrl.toString()
            Toast.makeText(context, "Api URL was changed to $apiUrl", Toast.LENGTH_SHORT).show()
        }
        api_add.setOnClickListener {
            val apiUrl = api_input.text.toString()
            apiList.add(apiUrl)
            arrayAdapter.add(apiUrl)
            arrayAdapter.notifyDataSetChanged()
            Toast.makeText(context, "Was added $apiUrl", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "MainFragment"
    }

}