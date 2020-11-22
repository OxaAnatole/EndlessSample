package com.oxagile.itapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.oxagile.itapp.BuildConfig
import com.oxagile.itapp.R
import com.oxagile.itapp.network.NetworkFactory
import com.oxagile.itapp.prefs.PrefsConstants.PERIOD_MINUTES_VALUE
import com.oxagile.itapp.prefs.PrefsConstants.PERIOD_MINUTES_KEY
import com.oxagile.itapp.utils.DeviceUtil
import com.oxagile.itapp.vm.MainViewModel
import com.oxagile.itapp.vm.MainViewModel.Companion.WORK_TAG
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private var started = false

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
        text_edit_period.setText(Prefs.getInt(PERIOD_MINUTES_KEY, PERIOD_MINUTES_VALUE).toString())

        serviceBtn.setOnClickListener {
            if (started) {
                mainViewModel.stopService()
                serviceBtn.text = getString(R.string.stop)
            } else {
                mainViewModel.startService()
                serviceBtn.text = getString(R.string.start)
            }
            started = !started
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
            Prefs.putInt(PERIOD_MINUTES_KEY, period)
            Toast.makeText(context, "Set period: $period minutes", Toast.LENGTH_SHORT).show()
        }
        WorkManager.getInstance(view.context).getWorkInfosByTagLiveData(WORK_TAG)
            .observe(viewLifecycleOwner, {
                val info: WorkInfo? = it.find { i -> i.tags.contains(WORK_TAG) }
                Log.d(TAG, "onViewCreated: info = ${info?.state}")
                if (info?.state == WorkInfo.State.ENQUEUED || info?.state == WorkInfo.State.RUNNING) {
                    started = true
                    serviceBtn.text = getString(R.string.stop)
                } else {
                    started = false
                    serviceBtn.text = getString(R.string.start)
                }
            })
    }

    companion object {
        val TAG = MainFragment::class.java.simpleName
    }

}