package com.romarickc.reminder.presentation.screens.settings

sealed class SettingsEvents {
    data class OnNotifValueChange(
        val q: Int,
    ) : SettingsEvents()

    data class OnLanguageChange(
        val q: Int,
    ) : SettingsEvents()

    object OnLanguageSettingsClick : SettingsEvents()

    object OnNotifSettingsClick : SettingsEvents()
}
