package com.romarickc.reminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Preferences(
    @PrimaryKey var id: Int? = null,
    var notifLevel: Int? = null,
)
