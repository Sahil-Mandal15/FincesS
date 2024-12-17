package com.sahilm.fincess.repository.auth

interface BaseAuth {

    suspend fun signIn(): Boolean

    suspend fun signOut()

    suspend fun isSignedIn():Boolean
}