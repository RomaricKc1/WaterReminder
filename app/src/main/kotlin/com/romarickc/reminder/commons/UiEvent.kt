package com.romarickc.reminder.commons

sealed class UiEvent {
    data class Navigate(
        val route: String,
    ) : UiEvent()

    object PopBackStack : UiEvent()
}
