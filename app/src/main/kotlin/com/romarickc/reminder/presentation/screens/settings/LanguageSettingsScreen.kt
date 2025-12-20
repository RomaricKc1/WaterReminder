package com.romarickc.reminder.presentation.screens.settings

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.romarickc.reminder.commons.Constants.LANG_EN
import com.romarickc.reminder.commons.Constants.LANG_FR
import com.romarickc.reminder.commons.E_Languages
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val currentLangPref = E_Languages.toValue(viewModel.currentLangPref)

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

    LanguageSettingsContent(
        onEvent = viewModel::onEvent,
        langPref = currentLangPref,
    )
    Log.i("langPref", "current lang -> $currentLangPref")
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageSettingsContent(
    onEvent: (SettingsEvents) -> Unit,
    langPref: Int,
) {
    val languages =
        listOf(
            E_Languages.FRANCAIS,
            E_Languages.ENGLISH,
        )

    val pickerState =
        rememberPickerState(
            initialNumberOfOptions = languages.size,
            initiallySelectedOption = langPref,
            repeatItems = false,
        )

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
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
                Text(text = stringResource(R.string.lang_settings), Modifier.padding(top = 29.dp))
            }

            // preference picker
            Picker(
                modifier =
                    Modifier
                        .height(97.dp),
                state = pickerState,
                contentDescription = "sel lang pref",
            ) {
                Text(
                    text = (
                        when (languages[it]) {
                            E_Languages.FRANCAIS -> LANG_FR
                            E_Languages.ENGLISH -> LANG_EN
                        }
                    ),
                    style =
                        TextStyle(
                            fontSize = 23.sp,
                            color = MyBlue,
                            fontWeight = FontWeight.Bold,
                        ),
                )
            }

            // confirm
            Button(
                modifier = Modifier,
                onClick = {
                    onEvent(
                        SettingsEvents.OnLanguageChange(
                            E_Languages.toValue(languages[pickerState.selectedOption]),
                        ),
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
                    contentDescription = "triggers chg lang",
                    tint = Color(0xFFf6f6f6),
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewLang() {
    ReminderTheme {
        LanguageSettingsContent(
            onEvent = {},
            langPref = E_Languages.toValue(E_Languages.FRANCAIS),
        )
    }
}
