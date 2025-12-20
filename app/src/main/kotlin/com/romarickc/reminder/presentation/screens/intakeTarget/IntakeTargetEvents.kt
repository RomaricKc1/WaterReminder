package com.romarickc.reminder.presentation.screens.intakeTarget

sealed class IntakeTargetEvents {
    data class OnValueChange(
        val q: Int,
    ) : IntakeTargetEvents()
}
