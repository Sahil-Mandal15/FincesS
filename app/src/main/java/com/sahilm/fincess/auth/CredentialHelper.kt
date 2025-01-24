package com.sahilm.fincess.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sahilm.fincess.BuildConfig
import com.sahilm.fincess.model.LoginState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

class CredentialHelper(
    private val context: Context
) {

    private var credentialManager: CredentialManager? = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
    private val tag = "CredentialHelper : "

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_TOKEN_ID = stringPreferencesKey("user_token_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHOTO_URI = stringPreferencesKey("user_photo_uri")
    }

    suspend fun saveUserDetails(
        isLoggedIn: Boolean,
        userEmail: String = "",
        tokenId: String = "",
        userName: String = "",
        userPhotoUri: String = ""
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_EMAIL] = userEmail
            preferences[USER_TOKEN_ID] = tokenId
            preferences[USER_NAME] = userName
            preferences[USER_PHOTO_URI] = userPhotoUri
        }
    }

    val loginState: Flow<LoginState> = context.dataStore.data.map { preferences ->
        LoginState(
            isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
            userEmail = preferences[USER_EMAIL] ?: "",
            tokenId = preferences[USER_TOKEN_ID] ?: "",
            userName = preferences[USER_NAME] ?: "",
            userPhotoUri = preferences[USER_PHOTO_URI] ?: ""
        )
    }


    suspend fun signIn(): Boolean {
        if (isSignedIn()) {
            return true
        }
        try {
            val result = getCredentialRequest()
            return handleSignIn(result)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e

            println(tag + "signIn error: ${e.message}")
            return false
        }
    }

    private suspend fun getCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(googleClientId)
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        return credentialManager!!.getCredential(context, request)
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                println(tag + "GoogleIdToken: ${tokenCredential.displayName}")
                println(tag + "GoogleIdToken: ${tokenCredential.id}")
                println(tag + "GoogleIdToken: ${tokenCredential.profilePictureUri}")
                println(tag + "GoogleIdToken: ${tokenCredential.idToken}")

                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                authResult.user?.let {
                    saveUserDetails(
                        isLoggedIn = true,
                        userEmail = it.email ?: "",
                        tokenId = tokenCredential.idToken,
                        userName = tokenCredential.displayName ?: "",
                        userPhotoUri = tokenCredential.profilePictureUri.toString()
                    )
                }
                println(tag + "DataStore: ${loginState.first()}")

                return authResult.user != null
            } catch (e: GoogleIdTokenParsingException) {
                println(tag + "GoogleIdTokenParsingException: ${e.message}")
                return false
            }
        } else {
            println(tag + "credential is not GoogleIdTokenCredential")
            return false
        }
    }

    fun isSignedIn(): Boolean {

        if (firebaseAuth.currentUser != null) {
            println(tag + "User is already signed in")
            return true
        }
        return false
    }

    suspend fun signOut() {
        credentialManager!!.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()

        saveUserDetails(
            isLoggedIn = false
        )
    }

     fun clearCredentialManager() {
        credentialManager = null
    }
}