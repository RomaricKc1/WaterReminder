package com.romarickc.reminder.di

import android.content.Context
import android.util.Log
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.loadServerAddress
import com.romarickc.reminder.commons.updateServerAddress
import com.romarickc.reminder.data.remote.Comm
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommModule {
    @Provides
    @Singleton
    fun provideComm(
        @ApplicationContext appContext: Context,
    ): Comm {
        lateinit var serverAddress: String

        val s = loadServerAddress(appContext).trimStart()
        if (s.startsWith("http://", ignoreCase = true) || s.startsWith("https://", ignoreCase = true)) {
            serverAddress = s
        } else {
            serverAddress = Constants.FALLBACK_SERVER_ADDR
            updateServerAddress(serverAddress, appContext)
            Log.i("error server", "error occurred. Invalid addr. Using default $serverAddress")
        }

        return Retrofit
            .Builder()
            .baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Comm::class.java)
    }
}
