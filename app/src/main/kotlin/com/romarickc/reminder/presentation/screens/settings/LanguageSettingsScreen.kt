package com.romarickc.reminder.presentation.screens.settings

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.Picker
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonColors
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants.LANG_EN
import com.romarickc.reminder.commons.Constants.LANG_FR
import com.romarickc.reminder.commons.E_Languages
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.theme.MyBlue
import com.romarickc.reminder.presentation.theme.ReminderTheme

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

                else -> {
                    Unit
                }
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
        androidx.wear.compose.material3.rememberPickerState(
            initialNumberOfOptions = languages.size,
            initiallySelectedIndex = langPref,
            shouldRepeatOptions = false,
        )
    AppScaffold {
        val listState = rememberTransformingLazyColumnState()

        ScreenScaffold(
            scrollState = listState,
            contentPadding =
                rememberResponsiveColumnPadding(
                    first = ColumnItemType.IconButton,
                    last = ColumnItemType.Button,
                ),
            edgeButton = {
            },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // title
                ListHeader(
                    modifier =
                        Modifier.fillMaxWidth().padding(top = 20.dp),
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.lang_settings),
                    )
                }

                // preference picker
                Picker(
                    modifier =
                        Modifier
                            .height(87.dp),
                    state = pickerState,
                    contentDescription = { "sel lang pref" },
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
                                fontWeight = Bold,
                            ),
                    )
                }

                // confirm btn
                TextButton(
                    modifier = Modifier,
                    onClick = {
                        onEvent(
                            SettingsEvents.OnLanguageChange(
                                E_Languages.toValue(languages[pickerState.selectedOptionIndex]),
                            ),
                        )
                    },
                    colors =
                        TextButtonColors(
                            containerColor = Color(0xFF2a2b2e),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Black,
                            disabledContentColor = Color.Black,
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
