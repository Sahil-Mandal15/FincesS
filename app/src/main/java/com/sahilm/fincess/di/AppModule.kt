package com.sahilm.fincess.di

import android.content.Context
import com.sahilm.fincess.repository.AuthRepository
import com.sahilm.fincess.repository.BaseAuthRepository
import com.sahilm.fincess.repository.auth.BaseAuth
import com.sahilm.fincess.repository.auth.GoogleAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGoogleAuthClient(@ApplicationContext context: Context): BaseAuth {
        return GoogleAuth(context)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        googleAuthClient: BaseAuth
    ) : BaseAuthRepository {
        return AuthRepository(googleAuthClient)
    }

}