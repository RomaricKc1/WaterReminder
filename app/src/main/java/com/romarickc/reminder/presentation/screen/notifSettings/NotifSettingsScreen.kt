package com.romarickc.reminder.presentation.screen.notifSettings

import android.util.Log
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch

@Composable
fun NotifSettingsScreen(
    viewModel: NotifSettingsViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val currentNotifPref = viewModel.currentPref.collectAsState(initial = 0).value

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

    NotifSettingsContent(onEvent = viewModel::onEvent, notifPref = currentNotifPref)
    Log.i("notifPref", "$currentNotifPref")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotifSettingsContent(
    onEvent: (NotifSettingsEvents) -> Unit,
    notifPref: Int,
) {
    val items = listOf("Every hour", "Every 3 hours", "Deactivated")

    val pickerState = rememberPickerState(
        initialNumberOfOptions = items.size,
        initiallySelectedOption = notifPref,
        repeatItems = false
    )

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
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
                Text(text = "Settings", Modifier.padding(top = 29.dp))
            }

            // preference picker
            Picker(
                modifier = Modifier
                    .height(97.dp),
                state = pickerState,
                contentDescription = "sel notif pref",
            ) {
                Text(
                    text = (items[it]),
                    style = TextStyle(
                        fontSize = 23.sp,
                        color = MyBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // confirm
            Button(
                modifier = Modifier,
                onClick = {
                    onEvent(NotifSettingsEvents.OnValueChange(pickerState.selectedOption))
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
        NotifSettingsContent(onEvent = {}, notifPref = 0)
    }
}