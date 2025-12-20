package com.romarickc.reminder.di

import com.romarickc.reminder.data.local.database.WaterIntakeDatabase
import com.romarickc.reminder.data.repository.WaterIntakeRepositoryImpl
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWaterIntakeRepository(db: WaterIntakeDatabase): WaterIntakeRepository =
        WaterIntakeRepositoryImpl(
            db.dao,
            db.dao2,
            db.dao3,
        )
}
