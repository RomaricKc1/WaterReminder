package com.romarickc.reminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Preferences(
    @PrimaryKey val id: Int? = null,
    val notifLevel: Int? = null
)