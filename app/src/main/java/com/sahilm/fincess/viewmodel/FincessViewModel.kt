package com.sahilm.fincess.viewmodel

import androidx.lifecycle.ViewModel
import com.sahilm.fincess.repository.BaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FincessViewModel @Inject constructor(
    private val authRepository: BaseAuthRepository
): ViewModel() {

    suspend fun signIn(): Boolean {
        return authRepository.signIn()
    }

    suspend fun signOut() {
        authRepository.signOut()
    }

    suspend fun isSignedIn(): Boolean {
        return authRepository.isSignedIn()
    }
}