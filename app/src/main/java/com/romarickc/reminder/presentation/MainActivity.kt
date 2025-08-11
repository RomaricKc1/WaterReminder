package com.romarickc.reminder.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.WaterReminderWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WaterReminderWorker.startPeriodicWork(this)
        setContent {
            WearApp()
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun WearApp() {
    ReminderTheme {
        NavigationM()
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ReminderTheme {
        WearApp()
    }
}
