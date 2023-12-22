package com.capstone.project.nutritionguardian.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.nutritionguardian.data.UserRepository
import com.capstone.project.nutritionguardian.data.pref.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean>
        get() = _loginResult

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.loginUser(email, password)
                _loginResult.postValue(true)
                saveSession(UserModel(user.email ?: "", ""))
            } catch (e: Exception) {
                _loginResult.postValue(false)
            }
        }
    }



    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}
