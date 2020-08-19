package com.oxagile.itapp.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.oxagile.itapp.BuildConfig
import com.oxagile.itapp.R
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.endless.EndlessService
import com.oxagile.itapp.utils.DeviceUtil
import com.oxagile.itapp.vm.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_main.*

const val PREFS_PERIOD_KEY = "period"
const val PERIOD_DEFAULT = 10

class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private var bound = false
    private var binder: EndlessService.ServiceBinder? = null
    private val intent: Intent by lazy { Intent(context, EndlessService::class.java) }
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            serviceBtn.isEnabled = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bound = true
            binder = service as EndlessService.ServiceBinder
            serviceBtn.isEnabled = true
            if (binder!!.serviceStarted) {
                serviceBtn.text = "Stop"
            } else {
                serviceBtn.text = "Start"
            }
        }
    }

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

        serviceBtn.setOnClickListener {
            if (binder!!.serviceStarted) {
                context.unbindService(connection)
                context.stopService(intent)
                serviceBtn.text = "Start"
            } else {
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
                context.startService(intent)
                serviceBtn.text = "Stop"
            }
        }

        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, mainViewModel.getUrlList())
        api_input.setAdapter(arrayAdapter)
        api_input.setText(NetworkFactory.BASE_URL)
        api_set.setOnClickListener {
            val apiUrl = api_input.text.toString()
            mainViewModel.setUrl(apiUrl)
            arrayAdapter.add(apiUrl)
            arrayAdapter.notifyDataSetChanged()
            Toast.makeText(context, "$apiUrl was added ", Toast.LENGTH_SHORT).show()
        }
        button_change_period.setOnClickListener {
            val period = text_edit_period.text.toString().toInt()
            Prefs.putInt(PREFS_PERIOD_KEY, period)
            Toast.makeText(context, "Set period: $period minutes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (bound) {
            serviceBtn.isEnabled = true
            if (binder!!.serviceStarted) {
                serviceBtn.text = "Stop"
            } else {
                serviceBtn.text = "Start"
            }
        } else {
            serviceBtn.isEnabled = false
        }
    }

    override fun onStop() {
        super.onStop()
        if (binder != null && binder!!.serviceStarted) {
            requireContext().unbindService(connection)
        }
    }

    companion object {
        const val TAG = "MainFragment"
    }

}