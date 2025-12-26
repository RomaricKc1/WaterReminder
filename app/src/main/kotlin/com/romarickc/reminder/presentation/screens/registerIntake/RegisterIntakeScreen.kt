package com.romarickc.reminder.presentation.screens.registerIntake

import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.screens.intakeTarget.IntakeTargetViewModel
import com.romarickc.reminder.presentation.theme.Grey500
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.MyBlue2
import com.romarickc.reminder.presentation.theme.ReminderTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun RegisterIntakeScreen(
    viewModel: RegisterIntakeViewModel = hiltViewModel(),
    viewModel2: IntakeTargetViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val intakesCntToday = viewModel.intakesToday.collectAsState(initial = 0).value
    val intakeTarget =
        viewModel2.currentTarget.collectAsState(initial = Constants.MIN_INTAKE).value

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

    RegisterIntakeContent(currentVal = intakesCntToday, intakeTarget, onEvent = viewModel::onEvent)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RegisterIntakeContent(
    currentVal: Int,
    intakeTarget: Int,
    onEvent: (RegisterIntakeEvents) -> Unit,
) {
    val waterAnimation =
        animateFloatAsState(
            targetValue =
                if (currentVal == 0) {
                    0f
                } else {
                    currentVal.toFloat() / intakeTarget.toFloat()
                },
            animationSpec = spring(DampingRatioLowBouncy),
            label = "",
        )

    Box(modifier = Modifier.fillMaxSize()) {
        Stepper(
            value = currentVal,
            onValueChange = {
                if (it > currentVal) {
                    onEvent(RegisterIntakeEvents.OnIncreaseClick(it))
                } else {
                    onEvent(RegisterIntakeEvents.OnDecreaseClick(it))
                }
            },
            valueProgression = 1..Constants.MAX_INTAKE,
            increaseIcon = {
                Icon(
                    imageVector = Icons.Rounded.LocalDrink,
                    contentDescription = "increase glass count",
                    modifier = Modifier.requiredSize(25.dp),
                )
            },
            decreaseIcon = {
                Icon(
                    StepperDefaults.Decrease,
                    "decrease glass count",
                )
            },
        ) {
            Chip(
                modifier =
                    Modifier
                        .padding(horizontal = 18.dp)
                        .testTag("current glass cnt"),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.LocalDrink,
                        contentDescription = "current glass cnt",
                        modifier = Modifier.requiredSize(31.dp),
                    )
                },
                colors =
                    ChipDefaults.chipColors(
                        backgroundColor = Grey500,
                    ),
                label = {
                    Text(
                        text = "$currentVal",
                        style =
                            TextStyle(
                                fontSize = 31.sp,
                                color = MyBlue,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                },
                onClick = { },
            )
        }

        CircularProgressIndicator(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(3.dp),
            progress = waterAnimation.value,
            trackColor = MyBlue2,
            indicatorColor = MyBlue,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        RegisterIntakeContent(currentVal = 7, 21, onEvent = {})
    }
}
