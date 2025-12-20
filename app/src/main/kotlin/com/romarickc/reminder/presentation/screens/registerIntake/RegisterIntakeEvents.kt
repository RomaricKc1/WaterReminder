package com.romarickc.reminder.presentation.screens.registerIntake

sealed class RegisterIntakeEvents {
    data class OnIncreaseClick(
        val q: Int,
    ) : RegisterIntakeEvents()

    data class OnDecreaseClick(
        val q: Int,
    ) : RegisterIntakeEvents()
}
