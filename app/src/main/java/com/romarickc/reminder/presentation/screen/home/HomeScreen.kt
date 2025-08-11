package com.romarickc.reminder.presentation.screen.home

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CrisisAlert
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
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
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.R
import com.romarickc.reminder.presentation.screen.intakeTarget.IntakeTargetViewModel
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.Constants
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    viewModel2: IntakeTargetViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit,
) {
    val intakesCnttoday = viewModel.intakesToday.collectAsState(initial = 0).value
    val intakeTarget =
        viewModel2.currentTarget.collectAsState(initial = Constants.MIN_INTAKE).value
    // Log.i("home_screen", "intake today cnt $intakesCnttoday")

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

    HomeContent(
        intakecnt = intakesCnttoday,
        intakeTarget = intakeTarget,
        onEvent = viewModel::onEvent,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeContent(
    intakecnt: Int,
    intakeTarget: Int,
    onEvent: (HomeScreenEvents) -> Unit,
) {
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
                    .fillMaxSize(),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            // title
            item {
                ListHeader {
                    Text(text = "Water Reminder")
                }
            }

            val intakepercentage = (intakecnt * 100) / intakeTarget
            val intakestr =
                if (intakecnt > 1) R.string.water_intakes else R.string.water_intake
            // intake overview and mod
            item {
                AppCard(
                    appImage = {
                        Icon(
                            imageVector = Icons.Rounded.LocalDrink,
                            contentDescription = "none",
                            modifier = Modifier.requiredSize(15.dp),
                        )
                    },
                    appName = { Text("Target", color = MaterialTheme.colors.primary) },
                    time = { Text("$intakepercentage%", color = MaterialTheme.colors.secondary) },
                    title = { Text("Daily intake", color = MaterialTheme.colors.onSurface) },
                    modifier =
                        Modifier
                            .padding(2.dp)
                            .padding(
                                top = 5.dp,
                            ),
                    onClick = { onEvent(HomeScreenEvents.OnAddNewIntakeClick) },
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text =
                                stringResource(
                                    intakestr,
                                    intakecnt,
                                    intakeTarget,
                                    String.format(Locale.FRANCE, "%.2f", intakecnt * 0.25),
                                ),
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }
            }

            // intake history
            item {
                Chip(
                    modifier =
                        Modifier
                            // .fillMaxWidth()
                            .padding(top = 10.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = "triggers water intake history action",
                            modifier = Modifier,
                        )
                    },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.onSurface,
                            text = "Intake History",
                        )
                    },
                    colors =
                        ChipDefaults.chipColors(
                            backgroundColor = MyBlue,
                        ),
                    onClick = {
                        onEvent(HomeScreenEvents.OnIntakeHistory)
                    },
                )
            }

            // header
            item {
                ListHeader {
                    Text(text = "History & Tips")
                }
            }

            // others hydration & intake history
            items(1) { index ->
                Chip(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                    icon = {
                        when (index) {
                            0 ->
                                Icon(
                                    imageVector = Icons.Rounded.History,
                                    contentDescription = "triggers water intake history action",
                                    modifier = Modifier,
                                )

                            1 ->
                                Icon(
                                    imageVector = Icons.Rounded.SelfImprovement,
                                    contentDescription = "triggers hydration tips action",
                                    modifier = Modifier,
                                )
                        }
                    },
                    colors =
                        ChipDefaults.chipColors(
                            backgroundColor = MyBlue,
                        ),
                    label = {
                        when (index) {
                            0 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = "Hydration Tips",
                                )
                            }
                        }
                    },
                    onClick = {
                        when (index) {
                            0 -> {
                                onEvent(HomeScreenEvents.OnHydraTips)
                            }
                        }
                    },
                )
            }

            // title
            item {
                ListHeader {
                    Text(text = "Settings")
                }
            }

            // daily target & notifications, & import/export
            items(3) { index ->
                Chip(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                    icon = {
                        when (index) {
                            0 ->
                                Icon(
                                    imageVector = Icons.Rounded.CrisisAlert,
                                    contentDescription = "triggers set target action",
                                    modifier = Modifier,
                                )

                            1 ->
                                Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = "triggers notifications settings action",
                                    modifier = Modifier,
                                )

                            2 ->
                                Icon(
                                    imageVector = Icons.Rounded.Save,
                                    contentDescription = "triggers Import/Export action",
                                    modifier = Modifier,
                                )
                        }
                    },
                    colors =
                        ChipDefaults.chipColors(
                            backgroundColor = MyBlue,
                        ),
                    label = {
                        when (index) {
                            0 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onSecondary,
                                    text = "Set Target",
                                )
                            }

                            1 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = "Notifications Settings",
                                )
                            }

                            2 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = "Import/Export data",
                                )
                            }
                        }
                    },
                    onClick = {
                        when (index) {
                            0 -> {
                                onEvent(HomeScreenEvents.OnSetTarget)
                            }

                            1 -> {
                                onEvent(HomeScreenEvents.OnNotifSettings)
                            }

                            2 -> {
                                onEvent(HomeScreenEvents.OnImportExportData)
                            }
                        }
                    },
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        HomeContent(
            intakecnt = 7,
            Constants.RECOMMENDED_INTAKE,
            onEvent = {},
        )
    }
}
