package com.romarickc.reminder.presentation.screen.rIntake

sealed class RegisterIntakeEvents {
    data class OnIncreaseclick(
        val q: Int,
    ) : RegisterIntakeEvents()

    data class OnDecreaseclick(
        val q: Int,
    ) : RegisterIntakeEvents()
}
