package com.capstone.project.nutritionguardian.di

import android.content.Context
import com.capstone.project.nutritionguardian.data.UserRepository
import com.capstone.project.nutritionguardian.data.pref.UserPreference
import com.capstone.project.nutritionguardian.data.pref.dataStore
import com.google.firebase.auth.FirebaseAuth

object Injection {
    fun provideRepository(context: Context, auth: FirebaseAuth): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref, auth)
    }
}