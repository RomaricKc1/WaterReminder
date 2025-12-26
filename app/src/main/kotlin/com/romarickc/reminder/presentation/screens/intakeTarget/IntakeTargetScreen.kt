package com.romarickc.reminder.presentation.screens.intakeTarget

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeTargetScreen(
    viewModel: IntakeTargetViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val intakeTarget =
        viewModel.currentTarget.collectAsState(initial = Constants.MIN_INTAKE).value

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
    // Log.i("intake target screen", "${intakeTarget.value}")
    IntakeTargetContent(onEvent = viewModel::onEvent, intakeTarget = intakeTarget)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeTargetContent(
    onEvent: (IntakeTargetEvents) -> Unit,
    intakeTarget: Int,
) {
    val items = (Constants.MIN_INTAKE..Constants.MAX_INTAKE).toList()

    val pickerState =
        rememberPickerState(
            initialNumberOfOptions = items.size,
            initiallySelectedOption = intakeTarget - Constants.MIN_INTAKE,
            repeatItems = false,
        )

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        modifier = Modifier.fillMaxSize(),
    ) {
        val coroutineScope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Column(
            modifier =
                Modifier
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            pickerState.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }.focusRequester(focusRequester)
                    .focusable()
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // header
            ListHeader {
                Text(
                    text = stringResource(R.string.target),
                    Modifier.padding(top = 17.dp),
                    style = TextStyle(fontWeight = Bold, fontSize = 17.sp),
                )
            }
            // target picker
            Picker(
                modifier =
                    Modifier
                        .height(97.dp),
                state = pickerState,
                contentDescription = "sel target value",
            ) {
                Text(
                    text = (items[it]).toString(),
                    style =
                        TextStyle(
                            fontSize = 44.sp,
                            color = MyBlue,
                            fontWeight = Bold,
                        ),
                )
            }
            // confirm btn
            Button(
                modifier = Modifier,
                onClick = {
                    onEvent(
                        IntakeTargetEvents
                            .OnValueChange(pickerState.selectedOption + Constants.MIN_INTAKE),
                    )
                },
                colors =
                    ButtonDefaults.primaryButtonColors(
                        backgroundColor = Color(0xFF2a2b2e),
                    ),
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "triggers mod target action",
                    tint = Color(0xFFf6f6f6),
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
        IntakeTargetContent(onEvent = {}, Constants.RECOMMENDED_INTAKE)
    }
}
