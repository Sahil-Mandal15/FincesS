package com.sahilm.fincess.model

data class LoginState(
    val isLoggedIn: Boolean,
    val userEmail: String = "",
    val tokenId: String = "",
    val userName: String = "",
    val userPhotoUri: String = ""
)
