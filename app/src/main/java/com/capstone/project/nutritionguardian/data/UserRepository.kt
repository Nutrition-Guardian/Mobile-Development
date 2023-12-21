package com.capstone.project.nutritionguardian.data

import com.capstone.project.nutritionguardian.data.pref.UserModel
import com.capstone.project.nutritionguardian.data.pref.UserPreference
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val auth: FirebaseAuth
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun loginUser(email: String, password: String): UserModel {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val user = authResult.user
        return UserModel(user?.email ?: "", "")
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            auth: FirebaseAuth
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, auth)
            }.also { instance = it }
    }
}
