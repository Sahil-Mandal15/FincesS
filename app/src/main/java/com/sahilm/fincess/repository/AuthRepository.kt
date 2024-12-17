package com.sahilm.fincess.repository

import com.sahilm.fincess.repository.auth.BaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val googleAuthClient: BaseAuth
) : BaseAuthRepository{
    override suspend fun signIn(): Boolean {
        return googleAuthClient.signIn()
    }

    override suspend fun signOut() {
        return googleAuthClient.signOut()
    }

    override suspend fun isSignedIn(): Boolean {
        return googleAuthClient.isSignedIn()
    }
}