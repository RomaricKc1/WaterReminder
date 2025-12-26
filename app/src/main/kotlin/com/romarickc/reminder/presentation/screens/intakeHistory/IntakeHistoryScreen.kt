package com.romarickc.reminder.presentation.screens.intakeHistory

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.getTimeAgo
import com.romarickc.reminder.commons.getTimeTxt
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeHistoryScreen(
    viewModel: IntakeHistoryViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit,
) {
    val intakes = viewModel.intakes.collectAsState(initial = listOf()).value

    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                else -> Unit
            }
        }
    })

    IntakeHistoryContent(intakes = intakes, onEvent = viewModel::onEvent)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeHistoryContent(
    intakes: List<WaterIntake>,
    onEvent: (IntakeHistoryEvents) -> Unit,
) {
    val now = ZonedDateTime.now()
    val startOfDayTimestamp = now.toLocalDate().atStartOfDay(now.zone).toInstant()
    val today = Instant.now()

    val filteredIntakes =
        intakes.asReversed().filter { intake ->
            val objTimestamp = Instant.ofEpochMilli((intake.timestamp!!))
            (objTimestamp == today) ||
                (
                    objTimestamp.isBefore(today) &&
                        objTimestamp.isAfter(
                            startOfDayTimestamp,
                        )
                )
        }
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
    ) {
        val coroutineScope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        ScalingLazyColumn(
            modifier =
                Modifier
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            scalingLazyListState.scrollBy(it.verticalScrollPixels)
                            scalingLazyListState.animateScrollBy(0f)
                        }
                        true
                    }.focusRequester(focusRequester)
                    .focusable()
                    .fillMaxSize()
                    .testTag("lazyColumnHistory"),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            // title
            item {
                ListHeader {
                    Text(text = stringResource(R.string.intake_history))
                }
            }

            // buttons for days and months graph view
            items(2) { index ->
                Chip(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                    icon = {
                        when (index) {
                            0 ->
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                    contentDescription = "days graph intakes",
                                )

                            1 ->
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                    contentDescription = "months graph intakes",
                                )
                        }
                    },
                    colors =
                        ChipDefaults.chipColors(
                            backgroundColor = MyBlue,
                        ),
                    label = {
                        when (index) {
                            0 ->
                                Text(text = stringResource(R.string.see_graph_days))
                            1 ->
                                Text(text = stringResource(R.string.see_graph_months))
                        }
                    },
                    onClick = {
                        when (index) {
                            0 ->
                                onEvent(IntakeHistoryEvents.OnSeeDaysIntakeGraphClick)
                            1 ->
                                onEvent(IntakeHistoryEvents.OnSeeMonthsIntakeGraphClick)
                        }
                    },
                )
            }

            // today' history
            item {
                ListHeader {
                    Text(text = stringResource(R.string.intakes_today))
                }
            }

            items(filteredIntakes) { intake ->
                intake.timestamp
                    ?.let { getTimeAgo(it) }
                    ?.let {
                        SimpleCard(
                            time = it,
                            title = getTimeTxt(intake.timestamp!!),
                        )
                    }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        IntakeHistoryContent(
            intakes =
                listOf(
                    WaterIntake(
                        0,
                        1674252311000,
                    ),
                    WaterIntake(
                        0,
                        1674251321000,
                    ),
                    WaterIntake(
                        0,
                        1674242331000,
                    ),
                    WaterIntake(
                        0,
                        1674152341000,
                    ),
                ),
            onEvent = {},
        )
    }
}
