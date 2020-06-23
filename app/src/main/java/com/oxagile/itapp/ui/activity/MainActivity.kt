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

    private val handler = Handler()
    private val runnable = Runnable {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, LoginFragment(), LoginFragment.TAG)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportFragmentManager.findFragmentByTag(MainFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, MainFragment(), MainFragment.TAG)
                .commit()
        }
        if (supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            runnable.run()
        }
    }

    override fun onStart() {
        super.onStart()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        if (supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(30))
        }
    }

    override fun onEnter() {
        val fragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
            hideKeyboard()
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

}
