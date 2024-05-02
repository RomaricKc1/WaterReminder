package com.romarickc.reminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WaterIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val timestamp: Long? = null
)