package com.romarickc.reminder.data.di

import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.data.repository.WaterIntakeRepositoryImpl
import com.romarickc.reminder.data.waterIntake_database.WaterIntakeDatabase

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
    fun provideWaterIntakeDatabase(db: WaterIntakeDatabase): WaterIntakeRepository =
        WaterIntakeRepositoryImpl(db.dao, db.dao2, db.dao3)
}