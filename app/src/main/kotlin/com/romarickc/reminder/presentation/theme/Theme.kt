package com.romarickc.reminder.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.MotionScheme
import androidx.wear.compose.material3.Shapes

@Suppress("ktlint:standard:function-naming")
@Composable
fun ReminderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme(),
        typography =
            androidx.wear.compose.material3
                .Typography(),
        shapes = Shapes(),
        motionScheme = MotionScheme.standard(),
        content = content,
    )
}
