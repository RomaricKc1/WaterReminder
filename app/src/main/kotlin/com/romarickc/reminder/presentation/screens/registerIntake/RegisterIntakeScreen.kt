package com.romarickc.reminder.presentation.screens.registerIntake

import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.Stepper
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonColors
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.screens.intakeTarget.IntakeTargetViewModel
import com.romarickc.reminder.presentation.theme.MyBlue
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

                else -> {
                    Unit
                }
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
            TextButton(
                modifier =
                    Modifier
                        .fillMaxWidth(.7F)
                        .testTag("current glass cnt"),
                onClick = {
                },
                colors =
                    TextButtonColors(
                        containerColor = Color(0xFF2a2b2e),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Black,
                        disabledContentColor = Color.Black,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalDrink,
                        contentDescription = "current glass cnt",
                        modifier = Modifier.requiredSize(31.dp),
                    )
                    Text(
                        text = "    ",
                        style = TextStyle(fontSize = 31.sp),
                    )
                    Text(
                        text = "$currentVal",
                        style =
                            TextStyle(
                                fontSize = 31.sp,
                                color = MyBlue,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }
            }
        }
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 6.dp,
            gapSize = 6.dp,
            colors = ringColor(),
            progress = { waterAnimation.value },
        )
    }
}

@Composable
private fun ringColor() =
    ProgressIndicatorDefaults.colors(
        indicatorColor = MyBlue,
        trackColor = Color.White,
    )

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        RegisterIntakeContent(currentVal = 7, 21, onEvent = {})
    }
}
