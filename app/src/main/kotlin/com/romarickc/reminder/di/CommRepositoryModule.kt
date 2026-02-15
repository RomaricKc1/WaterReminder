package com.romarickc.reminder.di

import com.romarickc.reminder.data.remote.Comm
import com.romarickc.reminder.data.repository.CommRepositoryImpl
import com.romarickc.reminder.domain.repository.CommRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommRepositoryModule {
    @Provides
    @Singleton
    fun provideCommRepository(comm: Comm): CommRepository =
        CommRepositoryImpl(
            comm,
        )
}
