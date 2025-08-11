package com.romarickc.reminder.presentation.screen.hTips

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
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.UiEvent
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun HydrationTipsScreen(
    viewModel: HydraTipsViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
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
    val tipsList =
        listOf(
            Tip(
                "Start your day with a glass of water",
                "Drinking a glass of water first thing in the morning can help kickstart your hydration for the day.",
            ),
            Tip(
                "Carry a water bottle with you",
                "Having a water bottle with you at all times can help remind you to drink water throughout the day.",
            ),
            Tip(
                "Drink water before and after exercise",
                "Staying hydrated before and after exercise can help you perform at your best and reduce the risk of dehydration.",
            ),
            Tip(
                "Eat water-rich foods",
                "Foods like watermelon, cucumbers, and strawberries can help you stay hydrated and add variety to your diet.",
            ),
            Tip("Use a reminder app", "Setting reminders to drink water throughout the day can help make hydration a habit."),
            Tip("Avoid sugary drinks", "Sugary drinks can dehydrate you and add unnecessary calories to your diet."),
            Tip(
                "Drink water when you feel hungry",
                "Sometimes thirst can be mistaken for hunger, so try drinking water before reaching for a snack.",
            ),
            Tip(
                "Drink water when you feel tired",
                "Fatigue can be a sign of dehydration, so try drinking water instead of reaching for caffeine or sugar.",
            ),
            Tip(
                "Flavor your water",
                "Adding fruit, herbs, or a splash of juice to your water can make it more appealing and help you drink more.",
            ),
            Tip("Drink water with meals", "Drinking water with meals can help aid digestion and keep you hydrated throughout the day."),
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
                    top = 21.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 10.dp,
                ),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            items(tipsList.size) { index ->
                SimpleCard(
                    "tip",
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
    theapp: String,
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
        appName = { Text(theapp, color = MaterialTheme.colors.primary) },
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
