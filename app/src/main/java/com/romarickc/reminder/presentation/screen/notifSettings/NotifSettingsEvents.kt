package com.romarickc.reminder.presentation.screen.notifSettings

sealed class NotifSettingsEvents {
    data class OnValueChange(val q: Int) : NotifSettingsEvents()
}