package com.romarickc.reminder.presentation.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonColors
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

                else -> {
                    Unit
                }
            }
        }
    })

    AboutContent(viewModel.versionName)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AboutContent(version: String) {
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
                            text = stringResource(R.string.about),
                        )
                    }
                }

                // version
                item {
                    TextButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(.8F),
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryDim,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "versionFakeBtn",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text =
                                    stringResource(R.string.version)
                                        .format(version),
                            )
                        }
                    }
                }

                // sources
                item {
                    AppCard(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
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
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        time = {
                            Text(
                                stringResource(R.string.version_short)
                                    .format(version),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        title = {
                            Text(
                                "RomaricKc1",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier,
                            )
                        },
                        onClick = { },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "https://github.com/RomaricKc1/WaterReminder",
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
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
