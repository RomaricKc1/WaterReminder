package com.romarickc.reminder.presentation.screens.registerIntake

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants.DB_NOTIF_LEVEL_IDX
import com.romarickc.reminder.commons.E_NotifPeriod
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.reSchedPeriodicWork
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class RegisterIntakeViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
        private val application: Application,
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
                is RegisterIntakeEvents.OnDecreaseClick -> {
                    onDecrease()
                }

                // increase
                is RegisterIntakeEvents.OnIncreaseClick -> {
                    onIncrease()
                }
            }
        }

        private fun onDecrease() {
            viewModelScope.launch {
                repository.removeLastIntake()
                Toast
                    .makeText(
                        application,
                        application.applicationContext
                            .getString(R.string.removed_last),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

        private fun onIncrease() {
            viewModelScope.launch {
                repository.insertIntake()
                Toast
                    .makeText(
                        application,
                        application.applicationContext.getString(R.string.registered),
                        Toast.LENGTH_SHORT,
                    ).show()

                // update the worker to not send any notification that is like a minute away
                Log.i("register intake notif", "called")
                val notifPref =
                    E_NotifPeriod.fromValue(
                        repository
                            .getNotifPref(DB_NOTIF_LEVEL_IDX)
                            .firstOrNull() ?: E_NotifPeriod.ONE_HOUR_MODE.value,
                    )
                Log.i("register intake notif", "called, notifpref -> $notifPref")
                reSchedPeriodicWork(
                    context = application,
                    notifPref = notifPref,
                    careAboutDisabled = false,
                )
            }
        }
    }
