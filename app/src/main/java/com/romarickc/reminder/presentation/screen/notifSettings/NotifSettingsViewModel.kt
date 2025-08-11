package com.romarickc.reminder.presentation.screen.notifSettings

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.romarickc.reminder.domain.model.Preferences
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.utils.UiEvent
import com.romarickc.reminder.presentation.utils.WaterReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NotifSettingsViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
        private val application: Application,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        var currentPref = repository.getNotifPref(1)

        init {
            viewModelScope.launch {
                repository.insertNotifPref(0)
            }
        }

        fun onEvent(event: NotifSettingsEvents) {
            when (event) {
                is NotifSettingsEvents.OnValueChange -> {
                    viewModelScope.launch {
                        repository.updateNotifPref(Preferences(1, event.q))
                        when (event.q) {
                            1 -> {
                                updatePeriodicWorkInterval(3)
                            }
                            2 -> {
                                WorkManager
                                    .getInstance(application)
                                    .cancelAllWorkByTag("water_reminder_periodic_work")
                                Log.i("settings", "Cancelled notif")

                                val workInfos =
                                    withContext(Dispatchers.IO) {
                                        WorkManager
                                            .getInstance(application)
                                            .getWorkInfosByTag("water_reminder_periodic_work")
                                            .get()
                                    }
                                Log.i("new worker infos settings", "$workInfos")
                            }
                            0 -> {
                                updatePeriodicWorkInterval(1)
                            }
                        }
                        // Log.i("settings", "notification pref updated ${event.q}")
                        _uiEvent.emit(UiEvent.PopBackStack)
                    }
                }
            }
        }

        private fun updatePeriodicWorkInterval(interval: Long) {
            Log.i("Setting notif", "called")
            var workInfos =
                WorkManager
                    .getInstance(application)
                    .getWorkInfosByTag("water_reminder_periodic_work")
                    .get()

            Log.i("worker settings", "current cancelling and recreating $workInfos")
            val workRequest =
                PeriodicWorkRequestBuilder<WaterReminderWorker>(interval, TimeUnit.HOURS)
                    .setInitialDelay(interval, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints
                            .Builder()
                            .setRequiresBatteryNotLow(true)
                            .build(),
                    ).addTag("water_reminder_periodic_work") // set the tag here
                    .build()

            WorkManager.getInstance(application).enqueueUniquePeriodicWork(
                "water_reminder_periodic_work",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest,
            )
            workInfos =
                WorkManager
                    .getInstance(application)
                    .getWorkInfosByTag("water_reminder_periodic_work")
                    .get()

            Log.i("worker settings", "new one -> $workInfos")
        }
    }
