package com.oxagile.itapp.ui.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.oxagile.itapp.R
import com.oxagile.itapp.ui.fragment.LoginFragment
import com.oxagile.itapp.ui.fragment.MainFragment
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), LoginFragment.LoginActionListener {

    private var entered = false
    private val handler = Handler()
    private val runnable = Runnable {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, LoginFragment(), LoginFragment.TAG)
            .commitAllowingStateLoss()
        entered = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportFragmentManager.findFragmentByTag(MainFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, MainFragment(), MainFragment.TAG)
                .commit()
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(ENTERED_KEY)) {
            entered = savedInstanceState.getBoolean(ENTERED_KEY)
        }
        if (!entered && supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            runnable.run()
        }
    }

    override fun onStart() {
        super.onStart()
        handler.removeCallbacks(runnable)
    }

    override fun onStop() {
        super.onStop()
        if (supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(30))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ENTERED_KEY, entered)
    }

    override fun onEnter() {
        val fragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
            hideKeyboard()
            entered = true
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = currentFocus
        if (view == null) {
            view = this as View
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val ENTERED_KEY = "entered"
    }

}
