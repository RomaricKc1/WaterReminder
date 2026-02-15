package com.romarickc.reminder.presentation.screens.hydrationTips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppCard
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.theme.ReminderTheme

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

                else -> {
                    Unit
                }
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
    AppScaffold {
        val listState = rememberTransformingLazyColumnState()
        val transformationSpec = rememberTransformationSpec()
        var more by remember { mutableStateOf(false) }
        var init by remember { mutableStateOf(true) }

        ScreenScaffold(
            scrollState = listState,
            contentPadding =
                rememberResponsiveColumnPadding(
                    first = ColumnItemType.IconButton,
                    last = ColumnItemType.Button,
                ),
            edgeButton = {
                if (init) {
                    EdgeButton(
                        onClick = {
                            more = true
                        },
                        buttonSize = EdgeButtonSize.Medium,
                    ) {
                        Text("more")
                    }
                }
            },
        ) { contentPadding ->

            TransformingLazyColumn(
                state = listState,
                contentPadding = contentPadding,
            ) {
                // title
                item {
                    ListHeader(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.tips),
                        )
                    }
                }

                items(tipsList.size) { index ->
                    if (index <= 2) {
                        SimpleCard(
                            stringResource(R.string.tip),
                            "",
                            title = tipsList[index].title,
                            content = tipsList[index].description,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec)
                                    .padding(2.dp),
                        )
                    }
                }

                if (more) {
                    init = false
                    items(tipsList.size) { index ->
                        if (index > 2) {
                            SimpleCard(
                                stringResource(R.string.tip),
                                "",
                                title = tipsList[index].title,
                                content = tipsList[index].description,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec)
                                        .padding(2.dp),
                            )
                        }
                    }
                }
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
    modifier: Modifier,
) {
    AppCard(
        modifier = modifier,
        appImage = {
            Icon(
                imageVector = Icons.Rounded.TipsAndUpdates,
                contentDescription = "triggers Tips action",
                modifier = Modifier.requiredSize(15.dp),
            )
        },
        appName = { Text(theApp, color = MaterialTheme.colorScheme.primary) },
        time = { Text(time, color = MaterialTheme.colorScheme.secondary) },
        title = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
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
