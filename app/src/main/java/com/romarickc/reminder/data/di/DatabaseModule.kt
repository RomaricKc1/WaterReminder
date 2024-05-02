package com.romarickc.reminder.data.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.romarickc.reminder.data.waterIntake_database.WaterIntakeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton

    fun provideWaterIntakeDatabase(application: Application): WaterIntakeDatabase =
        Room
            .databaseBuilder(
                application,
                WaterIntakeDatabase::class.java,
                WaterIntakeDatabase.DATABASE_NAME
            )
            .build()
}