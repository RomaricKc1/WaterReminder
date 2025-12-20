package com.romarickc.reminder.presentation.screens.intakeHistory

sealed class IntakeHistoryEvents {
    object OnSeeDaysIntakeGraphClick : IntakeHistoryEvents()

    object OnSeeMonthsIntakeGraphClick : IntakeHistoryEvents()
}
