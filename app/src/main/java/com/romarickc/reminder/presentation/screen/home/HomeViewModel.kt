package com.romarickc.reminder.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.utils.Routes
import com.romarickc.reminder.presentation.utils.UiEvent
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
        repository: WaterIntakeRepository,
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
                        UiEvent.Navigate(
                            route = Routes.REGISTER_INTAKE,
                        ),
                    )
                }

                // intake history
                is HomeScreenEvents.OnIntakeHistory -> {
                    emitEvent(
                        UiEvent.Navigate(
                            route = Routes.INTAKE_HISTORY,
                        ),
                    )
                }

                // hydratation tips
                is HomeScreenEvents.OnHydraTips -> {
                    emitEvent(
                        UiEvent.Navigate(
                            route = Routes.HYDRATION_TIPS,
                        ),
                    )
                }

                // set target
                is HomeScreenEvents.OnSetTarget -> {
                    emitEvent(
                        UiEvent.Navigate(
                            route = Routes.SET_TARGET,
                        ),
                    )
                }

                // notifs settings
                is HomeScreenEvents.OnNotifSettings -> {
                    emitEvent(
                        UiEvent.Navigate(
                            route = Routes.NOTIF_SETTINGS,
                        ),
                    )
                }

                // import/export data
                is HomeScreenEvents.OnImportExportData -> {
                    emitEvent(
                        UiEvent.Navigate(
                            route = Routes.IMPORT_EXPORT,
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
