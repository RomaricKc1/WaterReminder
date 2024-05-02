package com.romarickc.reminder.presentation.screen.importExport


import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch

@Composable
fun ImportExportScreen(
    viewModel: ImportExportViewModel = hiltViewModel(),
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

    ImportExportContent(onEvent = viewModel::onEvent)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImportExportContent(
    onEvent: (ImportExportEvents) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
    ) {
        val coroutineScope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit){focusRequester.requestFocus()}

        ScalingLazyColumn(
            modifier = Modifier
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        scalingLazyListState.scrollBy(it.verticalScrollPixels)
                        scalingLazyListState.animateScrollBy(0f)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
                .fillMaxSize(),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            // title
            item {
                ListHeader {
                    Text(text = "Import/Export data")
                }
            }

            // import/export
            items(2) { index ->
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    icon = {
                        when (index) {

                            0 -> Icon(
                                imageVector = Icons.Rounded.CloudDownload,
                                contentDescription = "triggers Import action",
                                modifier = Modifier
                            )

                            1 -> Icon(
                                imageVector = Icons.Rounded.CloudUpload,
                                contentDescription = "triggers Export action",
                                modifier = Modifier
                            )
                        }
                    },
                    label = {
                        when (index) {
                            0 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = "Import data"
                                )
                            }

                            1 -> {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = "Export data"
                                )
                            }
                        }
                    },
                    onClick = {
                        when (index) {
                            0 -> {
                                onEvent(ImportExportEvents.OnImportclick(1))
                            }

                            1 -> {
                                onEvent(ImportExportEvents.OnExportclick(1))
                            }
                        }
                    }
                )
            }

        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        ImportExportContent(onEvent = {})
    }
}