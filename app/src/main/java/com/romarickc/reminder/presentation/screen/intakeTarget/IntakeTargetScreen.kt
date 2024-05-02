package com.romarickc.reminder.presentation.screen.intakeTarget

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.Constants
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch

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
    // Log.i("intaketargetscreen", "${intakeTarget.value}")
    IntakeTargetContent(onEvent = viewModel::onEvent, intakeTarget = intakeTarget)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IntakeTargetContent(
    onEvent: (IntakeTargetEvents) -> Unit,
    intakeTarget: Int,
) {
    val items = (Constants.MIN_INTAKE..Constants.MAX_INTAKE).toList()

    val pickerState = rememberPickerState(
        initialNumberOfOptions = items.size,
        initiallySelectedOption = intakeTarget - Constants.MIN_INTAKE,
        repeatItems = false
    )

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        modifier = Modifier.fillMaxSize(),
    ) {
        val coroutineScope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit){focusRequester.requestFocus()}

        Column(
            modifier = Modifier
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        pickerState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // header
            ListHeader {
                Text(
                    text = "Target",
                    Modifier.padding(top = 17.dp),
                    style = TextStyle(fontWeight = Bold, fontSize = 17.sp)
                )
            }
            // targer picker
            Picker(
                modifier = Modifier
                    .height(97.dp),
                state = pickerState,
                contentDescription = "sel target value",
            ) {
                Text(
                    text = (items[it]).toString(),
                    style = TextStyle(
                        fontSize = 44.sp,
                        color = MyBlue,
                        fontWeight = Bold
                    )
                )
            }
            // confirm btn
            Button(
                modifier = Modifier,
                onClick = {
                    onEvent(IntakeTargetEvents.OnValueChange(pickerState.selectedOption + Constants.MIN_INTAKE))
                },
                colors = ButtonDefaults.primaryButtonColors(
                    backgroundColor = Color(0xFF2a2b2e),
                )
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = "triggers mod target action",
                    tint = Color(0xFFf6f6f6)
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        IntakeTargetContent(onEvent = {}, Constants.RECOMMENDED_INTAKE)
    }
}