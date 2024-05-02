package com.romarickc.reminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IntakeTarget(
    @PrimaryKey val id: Int? = null,
    val currentTarget: Int? = null
)