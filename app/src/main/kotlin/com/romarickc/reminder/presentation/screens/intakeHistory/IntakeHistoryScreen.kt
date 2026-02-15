package com.romarickc.reminder.presentation.screens.intakeHistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonColors
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.getTimeAgo
import com.romarickc.reminder.commons.getTimeTxt
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.ReminderTheme
import java.time.Instant
import java.time.ZonedDateTime

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeHistoryScreen(
    viewModel: IntakeHistoryViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit,
) {
    val intakes = viewModel.intakes.collectAsState(initial = listOf()).value

    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }

                else -> {
                    Unit
                }
            }
        }
    })

    IntakeHistoryContent(intakes = intakes, onEvent = viewModel::onEvent)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun IntakeHistoryContent(
    intakes: List<WaterIntake>,
    onEvent: (IntakeHistoryEvents) -> Unit,
) {
    val now = ZonedDateTime.now()
    val startOfDayTimestamp = now.toLocalDate().atStartOfDay(now.zone).toInstant()
    val today = Instant.now()

    val filteredIntakes =
        intakes.asReversed().filter { intake ->
            val objTimestamp = Instant.ofEpochMilli((intake.timestamp!!))
            (objTimestamp == today) ||
                (
                    objTimestamp.isBefore(today) &&
                        objTimestamp.isAfter(
                            startOfDayTimestamp,
                        )
                )
        }

    AppScaffold {
        val listState = rememberTransformingLazyColumnState()
        val transformationSpec = rememberTransformationSpec()

        ScreenScaffold(
            scrollState = listState,
            contentPadding =
                rememberResponsiveColumnPadding(
                    first = ColumnItemType.IconButton,
                    last = ColumnItemType.Button,
                ),
            edgeButton = {
                EdgeButton(
                    onClick = {
                    },
                    buttonSize = EdgeButtonSize.Medium,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "",
                        modifier = Modifier,
                    )

                    Text(
                        text = stringResource(R.string.end),
                    )
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
                            text = stringResource(R.string.intake_history),
                        )
                    }
                }

                // buttons for days and months graph view
                items(2) { index ->
                    TextButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                        onClick = {
                            when (index) {
                                0 -> {
                                    onEvent(IntakeHistoryEvents.OnSeeDaysIntakeGraphClick)
                                }

                                1 -> {
                                    onEvent(IntakeHistoryEvents.OnSeeMonthsIntakeGraphClick)
                                }
                            }
                        },
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryDim,
                                contentColor = MaterialTheme.colorScheme.onTertiary,
                                disabledContainerColor = Color.Black,
                                disabledContentColor = Color.Black,
                            ),
                    ) {
                        Row(
                            modifier =
                            Modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            when (index) {
                                0 -> {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                        contentDescription = "days graph intakes",
                                    )
                                }

                                1 -> {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                        contentDescription = "months graph intakes",
                                    )
                                }
                            }
                            when (index) {
                                0 -> {
                                    Text(
                                        text = stringResource(R.string.see_graph_days),
                                        textAlign = TextAlign.Center,
                                    )
                                }

                                1 -> {
                                    Text(
                                        text = stringResource(R.string.see_graph_months),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }

                // today' history
                item {
                    ListHeader(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.intakes_today),
                        )
                    }
                }

                items(filteredIntakes) { intake ->
                    intake.timestamp
                        ?.let { getTimeAgo(it) }
                        ?.let {
                            SimpleCard(
                                time = it,
                                title = getTimeTxt(intake.timestamp!!),
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
        IntakeHistoryContent(
            intakes =
                listOf(
                    WaterIntake(
                        0,
                        1674252311000,
                    ),
                    WaterIntake(
                        0,
                        1674251321000,
                    ),
                    WaterIntake(
                        0,
                        1674242331000,
                    ),
                    WaterIntake(
                        0,
                        1674152341000,
                    ),
                ),
            onEvent = {},
        )
    }
}
