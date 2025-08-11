package com.romarickc.reminder.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun ReminderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content,
    )
}
