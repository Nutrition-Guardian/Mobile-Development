package com.capstone.project.nutritionguardian.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.project.nutritionguardian.data.UserRepository
import com.capstone.project.nutritionguardian.di.Injection
import com.capstone.project.nutritionguardian.view.login.LoginViewModel
import com.capstone.project.nutritionguardian.view.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class ViewModelFactory(private val repository: UserRepository, private val auth: FirebaseAuth) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository, auth) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, auth: FirebaseAuth): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context, auth), auth)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

}