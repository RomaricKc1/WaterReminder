package com.romarickc.reminder.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme

val Purple200 = Color(0xFFBB86FC)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Red400 = Color(0xFFCF6679)
val Grey500 = Color(0xFF202124)
val MyBlue = Color(0xFF339aff)
val MyBlue2 = Color(0xFF15202f)

internal val Color: ColorScheme =
    ColorScheme(
        primary = Purple200,
        primaryDim = Purple700,
        secondary = Teal200,
        secondaryDim = Teal200,
        error = Red400,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onError = Color.Black,
    )
