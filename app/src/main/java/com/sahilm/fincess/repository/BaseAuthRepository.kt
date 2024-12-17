package com.sahilm.fincess.repository

interface BaseAuthRepository {
    suspend fun signIn(): Boolean

    suspend fun signOut()

    suspend fun isSignedIn(): Boolean
}