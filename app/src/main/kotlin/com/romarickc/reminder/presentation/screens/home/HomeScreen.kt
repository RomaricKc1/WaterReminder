package com.romarickc.reminder.presentation.screens.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.net.toUri
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
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.Constants.PERMISSION_TO_RQ
import com.romarickc.reminder.commons.Constants.STANDARD_GLASS_L
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.screens.intakeTarget.IntakeTargetViewModel
import com.romarickc.reminder.presentation.screens.settings.PermissionDialog
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    viewModel2: IntakeTargetViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit,
) {
    val intakesCntToday = viewModel.intakesToday.collectAsState(initial = 0).value
    val intakeTarget =
        viewModel2.currentTarget.collectAsState(initial = Constants.MIN_INTAKE).value
    // Log.i("home_screen", "intake today cnt $intakesCntToday")

    var showDialog by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                showDialog = !isGranted
                permissionGranted = isGranted
            },
        )

    LaunchedEffect(key1 = true, block = {
        requestPermissionLauncher.launch(PERMISSION_TO_RQ)
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                else -> Unit
            }
        }
    })

    val context = LocalContext.current
    val activity = context as? Activity
    val shouldShow =
        shouldShowRequestPermissionRationale(
            activity!!,
            PERMISSION_TO_RQ,
        )

    PermissionDialog(
        showThis = showDialog,
        shouldShowRationale = shouldShow,
        onOkClick = {
            showDialog = false
            Log.i("notif dia", "onOkClick, launching req")
            requestPermissionLauncher.launch(PERMISSION_TO_RQ)
        },
        onSettings = {
            val intent =
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    ("package:" + context.packageName).toUri(),
                )
            context.startActivity(intent)
            showDialog = false
        },
        onDismiss = {
            showDialog = false
        },
    )

    HomeContent(
        intakeCnt = intakesCntToday,
        intakeTarget = intakeTarget,
        permissionGranted = permissionGranted,
        onEvent = viewModel::onEvent,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeContent(
    intakeCnt: Int,
    intakeTarget: Int,
    permissionGranted: Boolean,
    onEvent: (HomeScreenEvents) -> Unit,
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    val intakeStr =
        if (intakeCnt > 1) {
            stringResource(R.string.water_intakes)
                .format(intakeCnt, intakeTarget, intakeCnt * STANDARD_GLASS_L)
        } else {
            stringResource(R.string.water_intake)
                .format(intakeCnt, intakeTarget, intakeCnt * STANDARD_GLASS_L)
        }
    val intakePercentage = (intakeCnt * 100) / intakeTarget

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
                    .testTag("lazyColumn"),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            // title
            item {
                ListHeader {
                    Text(text = stringResource(id = R.string.header_main_title))
                }
            }

            // permission state
            if (!permissionGranted) {
                item {
                    ListHeader {
                        Text(text = stringResource(R.string.permission_not_granted_no_notif))
                    }
                }
            }

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
                    appName = {
                        Text(
                            stringResource(R.string.target),
                            color = MaterialTheme.colors.primary,
                        )
                    },
                    time = {
                        Text("$intakePercentage%", color = MaterialTheme.colors.secondary)
                    },
                    title = {
                        Text(
                            stringResource(R.string.daily_intake),
                            color = MaterialTheme.colors.onSurface,
                        )
                    },
                    modifier =
                        Modifier
                            .padding(2.dp)
                            .padding(
                                top = 5.dp,
                            ),
                    onClick = { onEvent(HomeScreenEvents.OnAddNewIntakeClick) },
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = intakeStr, color = MaterialTheme.colors.primary)
                    }
                }
            }

            // intake history
            item {
                Chip(
                    modifier =
                        Modifier
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
                            text = stringResource(R.string.intake_history),
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
                    Text(text = stringResource(R.string.tips))
                }
            }

            // hydration tips
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
                                    text = stringResource(R.string.hydration_tips),
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
                    Text(text = stringResource(R.string.personalization))
                }
            }

            // daily target & notifications, lang, & import/export
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
                                    imageVector = Icons.Rounded.Settings,
                                    contentDescription = "triggers settings action",
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
                                    text = stringResource(R.string.set_target),
                                )
                            }

                            1 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = stringResource(R.string.settings),
                                )
                            }

                            2 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = stringResource(R.string.import_export),
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
                                onEvent(HomeScreenEvents.OnSettings)
                            }

                            2 -> {
                                onEvent(HomeScreenEvents.OnImportExportData)
                            }
                        }
                    },
                )
            }

            // header
            item {
                ListHeader {
                    Text(text = stringResource(R.string.misc))
                }
            }

            // about
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
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = "triggers about action",
                                    modifier = Modifier,
                                )
                        }
                    },
                    label = {
                        when (index) {
                            0 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onSecondary,
                                    text = stringResource(R.string.about),
                                )
                            }
                        }
                    },
                    onClick = {
                        when (index) {
                            0 -> {
                                onEvent(HomeScreenEvents.OnAbout)
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
            intakeCnt = 7,
            Constants.RECOMMENDED_INTAKE,
            permissionGranted = true,
            onEvent = {},
        )
    }
}
