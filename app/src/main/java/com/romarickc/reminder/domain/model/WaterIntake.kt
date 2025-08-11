package com.romarickc.reminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WaterIntake(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var timestamp: Long? = null,
)
