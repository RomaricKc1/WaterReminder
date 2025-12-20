package com.romarickc.reminder.presentation.screens.hydrationTips

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TipsAndUpdates
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
import androidx.wear.compose.material.Icon
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
fun HydrationTipsScreen(
    viewModel: HydraTipsViewModel = hiltViewModel(),
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
    val tipsList =
        listOf(
            Tip(
                stringResource(R.string.tip1_title),
                stringResource(R.string.tip1_desc),
            ),
            Tip(
                stringResource(R.string.tip2_title),
                stringResource(R.string.tip2_desc),
            ),
            Tip(
                stringResource(R.string.tip3_title),
                stringResource(R.string.tip3_desc),
            ),
            Tip(
                stringResource(R.string.tip4_title),
                stringResource(R.string.tip4_desc),
            ),
            Tip(
                stringResource(R.string.tip5_title),
                stringResource(R.string.tip5_desc),
            ),
            Tip(
                stringResource(R.string.tip6_title),
                stringResource(R.string.tip6_desc),
            ),
            Tip(
                stringResource(R.string.tip7_title),
                stringResource(R.string.tip7_desc),
            ),
            Tip(
                stringResource(R.string.tip8_title),
                stringResource(R.string.tip8_desc),
            ),
            Tip(
                stringResource(R.string.tip9_title),
                stringResource(R.string.tip9_desc),
            ),
        )
    HydrationTipsContent(tipsList)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HydrationTipsContent(tipsList: List<Tip>) {
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
            contentPadding =
                PaddingValues(
                    top = 51.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 50.dp,
                ),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            items(tipsList.size) { index ->
                SimpleCard(
                    stringResource(R.string.tip),
                    "${index + 1}",
                    title = tipsList[index].title,
                    content = tipsList[index].description,
                )
            }
        }
    }
}

data class Tip(
    val title: String,
    val description: String,
)

@Suppress("ktlint:standard:function-naming")
@Composable
fun SimpleCard(
    theApp: String,
    time: String,
    title: String,
    content: String,
) {
    AppCard(
        appImage = {
            Icon(
                imageVector = Icons.Rounded.TipsAndUpdates,
                contentDescription = "triggers Tips action",
                modifier = Modifier.requiredSize(15.dp),
            )
        },
        appName = { Text(theApp, color = MaterialTheme.colors.primary) },
        time = { Text(time, color = MaterialTheme.colors.secondary) },
        title = { Text(title, color = MaterialTheme.colors.onSurface) },
        modifier = Modifier.padding(2.dp),
        onClick = {},
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = content)
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    ReminderTheme {
        HydrationTipsContent( // onEvent = {}
            listOf(
                Tip("Tip 1", "Drink water before meals"),
                Tip("Tip 2", "Carry a water bottle with you"),
                Tip("Tip 3", "Drink water when you feel hungry"),
                Tip("Tip 4", "Drink water when you feel tired"),
                Tip("Tip 5", "Avoid sugary drinks"),
            ),
        )
    }
}
