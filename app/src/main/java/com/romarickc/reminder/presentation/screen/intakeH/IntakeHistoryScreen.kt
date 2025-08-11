package com.romarickc.reminder.presentation.screen.intakeH

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Water
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.Routes
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeHistoryScreen(
    viewModel: IntakeHistoryViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val intakes = viewModel.intakes.collectAsState(initial = listOf()).value

    // handle navigations there
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> {
                    onPopBackStack(event)
                }
                else -> Unit
            }
        }
    })

    IntakeHistoryContent(intakes = intakes)
}

fun getTimeAgo(timestamp: Long): String {
    // TODO, implement leap year version also
    val currentTime = Instant.now().toEpochMilli()
    val timeDiff = (currentTime - timestamp) / 1000
    // timeDiff in s
    return when {
        timeDiff < 60 -> "Just now"
        timeDiff < 3600 -> "${timeDiff / 60}m ago" // less than an hour (59m)
        timeDiff < 86400 -> "${timeDiff / 3600}h ago" // 23h
        timeDiff < 31536000 -> "${timeDiff / 86400}d ago" // 365
        else -> "${timeDiff / 31536000}y ago" // xyo
    }
}

fun getTimeTxt(timestamp: Long): String {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    return dateFormat.format(date)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeHistoryContent(intakes: List<WaterIntake>) {
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

    val navController2 = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController2,
        startDestination = Routes.INTAKE_HISTORY,
    ) {
        composable(Routes.INTAKE_HISTORY) {
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
                            .fillMaxSize(),
                    state = scalingLazyListState,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // title

                    item {
                        ListHeader {
                            Text(text = "History")
                        }
                    }
                    item {
                        Chip(
                            label = { Text(text = "See graph") },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                    contentDescription = "triggers graph intakes",
                                )
                            },
                            colors =
                                ChipDefaults.chipColors(
                                    backgroundColor = MyBlue,
                                ),
                            onClick = {
                                navController2.navigate(Routes.SEE_INTAKE_GRAPH)
                                // onEvent(IntakeHistoryEvents.OnSeeIntakeGraphClick)
                            },
                        )
                    }

                    item {
                        ListHeader {
                            Text(text = "Intakes Today")
                        }
                    }

                    items(filteredIntakes) { intake ->
                        intake.timestamp
                            ?.let { getTimeAgo(it) }
                            ?.let {
                                SimpleCard(
                                    time = it,
                                    title = getTimeTxt(intake.timestamp!!.toLong()),
                                )
                            }
                        /*?.let {
                        Chip(
                            label = { Text(text = it+" "+getTimeTxt(intake.timestamp.toLong())) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.Water,
                                    contentDescription = "triggers nothing",
                                )
                            },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = Color.Transparent
                            ),
                            onClick = { }
                        )
                    }*/
                    }
                }
            }
        }
        composable(Routes.SEE_INTAKE_GRAPH) {
            SeeIntakeGraphScreen(onPopBackStack = {
                navController2.popBackStack()
            })
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
        appName = { Text("intake", color = MaterialTheme.colors.primary) },
        time = { Text(time, color = MaterialTheme.colors.secondary) },
        title = { Text(title, color = MaterialTheme.colors.onSurface) },
        modifier = Modifier.padding(2.dp),
        onClick = {},
    ) {
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
        )
    }
}
