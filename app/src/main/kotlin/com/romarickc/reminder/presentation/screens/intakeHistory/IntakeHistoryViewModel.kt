package com.romarickc.reminder.presentation.screens.intakeHistory

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Water
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.material3.AppCard
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Routes
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.UiEvent.Navigate
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

val paint =
    android.graphics.Paint().apply {
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = 20f
        color = Color.White.toArgb()
    }
val paint2 =
    android.graphics.Paint().apply {
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = 15f
        color = Color.White.toArgb()
    }
val paint3 =
    android.graphics.Paint().apply {
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = 15f
        color = Color.White.toArgb()
    }

val monthNames =
    arrayOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sept",
        "Oct",
        "Nov",
        "Dec",
    )

@HiltViewModel
class IntakeHistoryViewModel
    @Inject
    constructor(
        repository: WaterIntakeRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        var intakes = repository.getAllIntake()

        fun onEvent(event: IntakeHistoryEvents) {
            when (event) {
                // days graph
                is IntakeHistoryEvents.OnSeeDaysIntakeGraphClick -> {
                    emitEvent(
                        Navigate(
                            route = Routes.SEE_DAYS_INTAKE_GRAPH,
                        ),
                    )
                    println("navigating to days graph")
                }

                // days graph
                is IntakeHistoryEvents.OnSeeMonthsIntakeGraphClick -> {
                    emitEvent(
                        Navigate(
                            route = Routes.SEE_MONTHS_INTAKE_GRAPH,
                        ),
                    )
                    println("navigating to months graph")
                }
            }
        }

        private fun emitEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.emit(event)
            }
        }
    }

@Suppress("ktlint:standard:function-naming")
@Composable
fun SimpleCard(
    time: String,
    title: String,
) {
    AppCard(
        appImage = {
            Icon(
                imageVector = Icons.Rounded.Water,
                contentDescription = "triggers nothing",
                modifier = Modifier.requiredSize(15.dp),
            )
        },
        appName = { Text(stringResource(R.string.intake), color = MaterialTheme.colorScheme.primary) },
        time = { Text(time, color = MaterialTheme.colorScheme.secondary) },
        title = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
        modifier = Modifier.padding(2.dp),
        onClick = {},
    ) {
    }
}
