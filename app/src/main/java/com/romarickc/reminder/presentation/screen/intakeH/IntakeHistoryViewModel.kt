package com.romarickc.reminder.presentation.screen.intakeH

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.utils.Routes
import com.romarickc.reminder.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class IntakeHistoryViewModel
    @Inject
    constructor(
        repository: WaterIntakeRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        var intakes = repository.getAllIntake()

        var monthIntakes =
            repository.getPeriodWaterIntake(
                getCurrentMonthTimestamps().first,
                getCurrentMonthTimestamps().second,
            )
    /*var yearIntakes = repository.getPeriodWaterIntake(
        getCurrentMonthTimestamps().first,
        getCurrentMonthTimestamps().second
    )*/

        fun onEvent(event: IntakeHistoryEvents) {
            when (event) {
                // does not work, yes, replaced by something else in the main ig
                is IntakeHistoryEvents.OnSeeIntakeGraphClick -> {
                    emitEvent(
                        UiEvent.Navigate(
                            route = Routes.SEE_INTAKE_GRAPH,
                        ),
                    )
                    // Log.i("graph", "emit")
                }
            }
        }

        private fun emitEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.emit(event)
            }
        }

        private fun getCurrentMonthTimestamps(): Pair<Long, Long> {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
            calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))
            val startTimestamp = calendar.timeInMillis

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))
            calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND))
            val endTimestamp = calendar.timeInMillis

            return Pair(startTimestamp, endTimestamp)
        }
    }
