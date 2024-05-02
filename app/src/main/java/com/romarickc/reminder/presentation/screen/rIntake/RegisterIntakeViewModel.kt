package com.romarickc.reminder.presentation.screen.rIntake

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.utils.UiEvent
import com.romarickc.reminder.presentation.utils.WaterReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RegisterIntakeViewModel @Inject constructor(
    private val repository: WaterIntakeRepository,
    private val application: Application
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val now: ZonedDateTime = ZonedDateTime.now()
    private val startOfDay: ZonedDateTime = now.toLocalDate().atStartOfDay(now.zone)
    private val startOfDayTimestamp = startOfDay.toInstant().toEpochMilli()

    var intakesToday = repository.getCountTgtThis(startOfDayTimestamp)

    fun onEvent(event: RegisterIntakeEvents) {
        when (event) {
            // decrease
            is RegisterIntakeEvents.OnDecreaseclick -> {
                viewModelScope.launch {
                    repository.removeLastIntake()
                    Toast.makeText(application, "Removed last", Toast.LENGTH_SHORT).show()

                }
            }

            // increase
            is RegisterIntakeEvents.OnIncreaseclick -> {
                viewModelScope.launch {
                    repository.insertIntake()
                    Toast.makeText(application, "Registered", Toast.LENGTH_SHORT).show()

                    // update the worker to not send any notification that is like a minute away
                    // Toast.makeText(application, "$notifPref", Toast.LENGTH_SHORT).show()
                    when (repository.getNotifPref(1).firstOrNull() ?: 0) {
                        1 -> {
                            // the 3 hours interval
                            updatePeriodicWorkInterval(3)
                        }
                        2 -> {
                            // notification disabled, so I don't care
                        }
                        0 -> {
                            // the 1 hour interval
                            updatePeriodicWorkInterval(1)
                        }
                    }
                    // Log.i("inserted", "current policy -> $currentNotifSetting")
                }
            }
        }
    }

    private fun updatePeriodicWorkInterval(interval: Long) {
        Log.i("register intake notif", "called")
        var workInfos =
            WorkManager.getInstance(application)
                .getWorkInfosByTag("water_reminder_periodic_work").get()

        Log.i("worker settings", "current cancelling and recreating $workInfos")
        val workRequest =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(interval, TimeUnit.HOURS)
                .setInitialDelay(interval, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .addTag("water_reminder_periodic_work") // set the tag here
                .build()

        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            "water_reminder_periodic_work",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
        workInfos =
            WorkManager.getInstance(application)
                .getWorkInfosByTag("water_reminder_periodic_work").get()

        Log.i("worker register intake", "new one -> $workInfos")
    }
}