package com.oxagile.itapp.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oxagile.itapp.network.Result
import com.oxagile.itapp.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val repository = Repository()
    val loginLiveData = MutableLiveData<Boolean>()
    val messageLiveData = MutableLiveData<String>()

    fun onEnterClick(password: String) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = repository.checkPassword(password)) {
            is Result.Success -> {
                loginLiveData.postValue(result.data)
            }
            is Result.Error -> {
                messageLiveData.postValue(result.exception.message)
            }
        }
    }

}