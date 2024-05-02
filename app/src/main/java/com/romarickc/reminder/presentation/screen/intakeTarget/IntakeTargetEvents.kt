package com.romarickc.reminder.presentation.screen.intakeTarget

sealed class IntakeTargetEvents {
    data class OnValueChange(val q: Int) : IntakeTargetEvents()
}