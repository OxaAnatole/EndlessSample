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
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.endless.EndlessService
import com.oxagile.itapp.utils.DeviceUtil
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_main.*

const val PREFS_URL_KEY = "url"
const val PREFS_PERIOD_KEY = "period"
const val PERIOD_DEFAULT = 10

class MainFragment : Fragment() {

    private val apiList = mutableListOf(
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
        text_edit_period.setText(Prefs.getInt(PREFS_PERIOD_KEY, PERIOD_DEFAULT).toString())

        start.setOnClickListener {
            context.startService(Intent(context, EndlessService::class.java))
        }
        stop.setOnClickListener {
            val intent = Intent(context, EndlessService::class.java)
            context.stopService(intent)
        }

        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, apiList)
        api_input.setAdapter(
            arrayAdapter
        )

        api_input.setText(NetworkFactory.BASE_URL)
        api_change.setOnClickListener {
            val apiUrl = api_input.text
            Prefs.putString(PREFS_URL_KEY, apiUrl.toString())
            Toast.makeText(context, "Api URL was changed to $apiUrl", Toast.LENGTH_SHORT).show()
        }
        api_add.setOnClickListener {
            val apiUrl = api_input.text.toString()
            apiList.add(apiUrl)
            arrayAdapter.add(apiUrl)
            arrayAdapter.notifyDataSetChanged()
            Toast.makeText(context, "Was added $apiUrl", Toast.LENGTH_SHORT).show()
        }
        button_change_period.setOnClickListener {
            val period = text_edit_period.text.toString().toInt()
            Prefs.putInt(PREFS_PERIOD_KEY, period)
            Toast.makeText(context, "Set period: $period minutes", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "MainFragment"
    }

}