package com.romarickc.reminder.presentation.screen.intakeH

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Water
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.Routes
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

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

    IntakeHistoryContent(intakes = intakes, viewModel::onEvent)
}

fun getTimeAgo(timestamp: Long): String {
    // TODO, implement leap year version also
    val currentTime = Instant.now().toEpochMilli()
    val timeDiff = (currentTime - timestamp) / 1000
    //timeDiff in s
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IntakeHistoryContent(
    intakes: List<WaterIntake>,
    onEvent: (IntakeHistoryEvents) -> Unit,
) {
    val now = ZonedDateTime.now()
    val startOfDayTimestamp = now.toLocalDate().atStartOfDay(now.zone).toInstant()
    val today = Instant.now()

    val filteredIntakes = intakes.asReversed().filter { intake ->
        val objTimestamp = Instant.ofEpochMilli((intake.timestamp!!))
        (objTimestamp == today) || (objTimestamp.isBefore(today) && objTimestamp.isAfter(
            startOfDayTimestamp
        ))
    }
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()

    val navController2 = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController2,
        startDestination = Routes.IntakeHistory
    ) {
        composable(Routes.IntakeHistory) {
            Scaffold(
                timeText = { TimeText() },
                vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
                positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) }
            ) {
                val coroutineScope = rememberCoroutineScope()
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit){focusRequester.requestFocus()}

                ScalingLazyColumn(
                    modifier = Modifier
                        .onRotaryScrollEvent {
                            coroutineScope.launch {
                                scalingLazyListState.scrollBy(it.verticalScrollPixels)
                                scalingLazyListState.animateScrollBy(0f)
                            }
                            true
                        }
                        .focusRequester(focusRequester)
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
                                    imageVector = Icons.Rounded.TrendingUp,
                                    contentDescription = "triggers graph intakes",
                                )
                            },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MyBlue
                            ),
                            onClick = {
                                navController2.navigate(Routes.SeeIntakeGraph)
                                // onEvent(IntakeHistoryEvents.OnSeeIntakeGraphClick)
                            }
                        )
                    }

                    item {
                        ListHeader {
                            Text(text = "Intakes Today")
                        }
                    }

                    items(filteredIntakes) { intake ->
                        intake.timestamp?.let { getTimeAgo(it) }
                            ?.let {
                                SimpleCard(
                                    time = it,
                                    title = getTimeTxt(intake.timestamp.toLong())
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
        composable(Routes.SeeIntakeGraph){
            SeeIntakeGraphScreen(onPopBackStack = {
                navController2.popBackStack()
            })
        }
    }
}

@Composable
fun SimpleCard(time: String, title: String) {
    AppCard(
        appImage = {
            Icon(
                imageVector = Icons.Rounded.Water,
                contentDescription = "triggers nothing",
                modifier = Modifier.requiredSize(15.dp)
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

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        IntakeHistoryContent(
            intakes = listOf(
                WaterIntake(
                    0, 1674252311000
                ),
                WaterIntake(
                    0, 1674251321000
                ),
                WaterIntake(
                    0, 1674242331000
                ),
                WaterIntake(
                    0, 1674152341000
                )
            )
        ) {}
    }
}