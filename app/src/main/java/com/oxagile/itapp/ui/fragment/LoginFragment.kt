package com.oxagile.itapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.oxagile.itapp.R
import com.oxagile.itapp.vm.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment() {

    private var listener: LoginActionListener? = null
    private val viewModel: LoginViewModel by viewModels()
    private val watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            login.isEnabled = s?.isNotEmpty() ?: false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        password.addTextChangedListener(watcher)
        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            viewModel.onEnterClick(password.text.toString())
        }
        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer { success ->
            loading.visibility = View.GONE
            if (success) {
                listener?.onEnter()
            } else {
                Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.messageLiveData.observe(viewLifecycleOwner, Observer { message ->
            loading.visibility = View.GONE
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LoginActionListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "Activity must implements LoginActionListener", e)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface LoginActionListener {
        fun onEnter()
    }

    companion object {
        const val TAG = "LoginFragment"
    }

}