package com.romarickc.reminder.presentation.screens.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.commons.Routes
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.UiEvent.Navigate
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
        private val application: Application,
    ) : ViewModel() {
        private val now: ZonedDateTime = ZonedDateTime.now()
        private val startOfDay: ZonedDateTime = now.toLocalDate().atStartOfDay(now.zone)
        private val startOfDayTimestamp = startOfDay.toInstant().toEpochMilli()

        var intakesToday = repository.getCountTgtThis(startOfDayTimestamp)

        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        fun onEvent(event: HomeScreenEvents) {
            when (event) {
                // register intake
                is HomeScreenEvents.OnAddNewIntakeClick -> {
                    emitEvent(
                        Navigate(
                            route = Routes.REGISTER_INTAKE,
                        ),
                    )
                }

                // intake history
                is HomeScreenEvents.OnIntakeHistory -> {
                    emitEvent(
                        Navigate(
                            route = Routes.INTAKE_HISTORY,
                        ),
                    )
                }

                // hydration tips
                is HomeScreenEvents.OnHydraTips -> {
                    emitEvent(
                        Navigate(
                            route = Routes.HYDRATION_TIPS,
                        ),
                    )
                }

                // set target
                is HomeScreenEvents.OnSetTarget -> {
                    emitEvent(
                        Navigate(
                            route = Routes.SET_TARGET,
                        ),
                    )
                }

                // settings
                is HomeScreenEvents.OnSettings -> {
                    emitEvent(
                        Navigate(
                            route = Routes.SETTINGS,
                        ),
                    )
                }

                // import/export data
                is HomeScreenEvents.OnImportExportData -> {
                    emitEvent(
                        Navigate(
                            route = Routes.IMPORT_EXPORT,
                        ),
                    )
                }

                // about
                HomeScreenEvents.OnAbout -> {
                    emitEvent(
                        Navigate(
                            route = Routes.ABOUT,
                        ),
                    )
                }
            }
        }

        private fun emitEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.emit(event)
            }
        }
    }
