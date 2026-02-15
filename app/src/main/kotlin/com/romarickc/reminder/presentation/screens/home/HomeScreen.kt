package com.romarickc.reminder.presentation.screens.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppCard
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonColors
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.Constants.PERMISSION_TO_RQ
import com.romarickc.reminder.commons.Constants.STANDARD_GLASS_L
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.screens.intakeTarget.IntakeTargetViewModel
import com.romarickc.reminder.presentation.screens.settings.PermissionDialog
import com.romarickc.reminder.presentation.theme.ReminderTheme

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

                else -> {
                    Unit
                }
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
    val intakeStr =
        if (intakeCnt > 1) {
            stringResource(R.string.water_intakes)
                .format(intakeCnt, intakeTarget, intakeCnt * STANDARD_GLASS_L)
        } else {
            stringResource(R.string.water_intake)
                .format(intakeCnt, intakeTarget, intakeCnt * STANDARD_GLASS_L)
        }
    val intakePercentage = (intakeCnt * 100) / intakeTarget

    AppScaffold {
        val listState = rememberTransformingLazyColumnState()
        val transformationSpec = rememberTransformationSpec()

        ScreenScaffold(
            scrollState = listState,
            contentPadding =
                rememberResponsiveColumnPadding(
                    first = ColumnItemType.IconButton,
                    last = ColumnItemType.Button,
                ),
            edgeButton = {
                EdgeButton(
                    onClick = {
                        onEvent(HomeScreenEvents.OnAbout)
                    },
                    buttonSize = EdgeButtonSize.Medium,
                    modifier = Modifier.testTag("About"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "triggers about action",
                    )

                    Text(
                        text = stringResource(R.string.about),
                    )
                }
            },
        ) { contentPadding ->

            TransformingLazyColumn(
                state = listState,
                contentPadding = contentPadding,
                modifier = Modifier.testTag("lazyColumn"),
            ) {
                // title
                item {
                    ListHeader(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(id = R.string.header_main_title),
                        )
                    }
                }

                // permission state
                if (!permissionGranted) {
                    item {
                        ListHeader(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.permission_not_granted_no_notif),
                            )
                        }
                    }
                }

                // intake overview and mod
                item {
                    AppCard(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .padding(2.dp),
                        transformation = SurfaceTransformation(transformationSpec),
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
                            )
                        },
                        time = {
                            Text("$intakePercentage%")
                        },
                        colors = CardDefaults.cardColors(),
                        /*CardColors(
                            containerColor = MaterialTheme.colorScheme.primaryDim,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            appNameColor = MaterialTheme.colorScheme.onPrimary,
                            timeColor = MaterialTheme.colorScheme.tertiary,
                            titleColor = MaterialTheme.colorScheme.onPrimary,
                            subtitleColor = MaterialTheme.colorScheme.onSecondary,
                        ),*/
                        title = {
                            Text(
                                stringResource(R.string.daily_intake),
                            )
                        },
                        onClick = { onEvent(HomeScreenEvents.OnAddNewIntakeClick) },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = intakeStr)
                        }
                    }
                }

                // intake history
                item {
                    TextButton(
                        onClick = {
                            onEvent(HomeScreenEvents.OnIntakeHistory)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryDim,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = Color.Black,
                                disabledContentColor = Color.Black,
                            ),
                    ) {
                        Row(
                            modifier =
                            Modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = "triggers water intake history action",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.intake_history),
                            )
                        }
                    }
                }

                // header
                item {
                    ListHeader(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.tips),
                        )
                    }
                }

                // hydration tips
                item {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onEvent(HomeScreenEvents.OnHydraTips)
                        },
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryDim,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                disabledContainerColor = Color.Black,
                                disabledContentColor = Color.Black,
                            ),
                    ) {
                        Row(
                            modifier =
                            Modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SelfImprovement,
                                contentDescription = "triggers hydration tips action",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.hydration_tips),
                            )
                        }
                    }
                }

                // header
                item {
                    ListHeader(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.personalization),
                        )
                    }
                }

                // daily target & notifications, lang, & import/export
                items(3) { index ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(.8F),
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
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryDim,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                disabledContainerColor = Color.Black,
                                disabledContentColor = Color.Black,
                            ),
                    ) {
                        Row(
                            modifier =
                            Modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            when (index) {
                                0 -> {
                                    Icon(
                                        imageVector = Icons.Rounded.CrisisAlert,
                                        contentDescription = "triggers set target action",
                                    )
                                }

                                1 -> {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "triggers settings action",
                                    )
                                }

                                2 -> {
                                    Icon(
                                        imageVector = Icons.Rounded.Save,
                                        contentDescription = "triggers Import/Export action",
                                    )
                                }
                            }
                            when (index) {
                                0 -> {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.set_target),
                                    )
                                }

                                1 -> {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.settings),
                                    )
                                }

                                2 -> {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.import_export),
                                    )
                                }
                            }
                        }
                    }
                }

                // header
                item {
                    ListHeader(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.misc),
                        )
                    }
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
        HomeContent(
            intakeCnt = 7,
            Constants.RECOMMENDED_INTAKE,
            permissionGranted = true,
            onEvent = {},
        )
    }
}
