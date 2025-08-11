package com.romarickc.reminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IntakeTarget(
    @PrimaryKey var id: Int? = null,
    var currentTarget: Int? = null,
)
