package com.romarickc.reminder.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.commons.Constants.ONE_HOUR_INTERVAL
import com.romarickc.reminder.commons.WaterReminderWorker
import com.romarickc.reminder.commons.WorkerConf
import com.romarickc.reminder.commons.loadLanguage
import com.romarickc.reminder.presentation.theme.ReminderTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workerConf =
            WorkerConf(
                timeUnit = TimeUnit.HOURS,
                onlyOnNotLowBattery = true,
                interval = ONE_HOUR_INTERVAL.toLong(),
            )
        WaterReminderWorker.initWorker(context = this, workerConf = workerConf)
        loadLanguage(this)

        setContent {
            WearApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ktlint:standard:function-naming")
@Composable
fun WearApp() {
    ReminderTheme {
        NavigationM()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ReminderTheme {
        WearApp()
    }
}
