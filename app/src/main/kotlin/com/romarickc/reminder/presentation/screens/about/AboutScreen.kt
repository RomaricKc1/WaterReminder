package com.romarickc.reminder.presentation.screens.about

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
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.theme.ReminderTheme
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun AboutScreen(
    viewModel: AboutViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
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

    AboutContent(viewModel.versionName)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AboutContent(version: String) {
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
                    Text(text = stringResource(R.string.about))
                }
            }

            // version
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
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = "versionFakeBtn",
                                    modifier = Modifier,
                                )
                        }
                    },
                    label = {
                        when (index) {
                            0 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text =
                                        stringResource(R.string.version)
                                            .format(version),
                                )
                            }
                        }
                    },
                    onClick = {
                    },
                )
            }

            // sources
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
                            stringResource(R.string.source_code),
                            color = MaterialTheme.colors.primary,
                        )
                    },
                    time = {
                        Text(
                            stringResource(R.string.version_short)
                                .format(version),
                            color = MaterialTheme.colors.secondary,
                        )
                    },
                    title = {
                        Text(
                            "RomaricKc1",
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier,
                        )
                    },
                    modifier =
                        Modifier
                            .padding(2.dp)
                            .padding(
                                top = 5.dp,
                            ),
                    onClick = { },
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "https://github.com/RomaricKc1/WaterReminder",
                            color = MaterialTheme.colors.primary,
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
        AboutContent("1.1.1")
    }
}
