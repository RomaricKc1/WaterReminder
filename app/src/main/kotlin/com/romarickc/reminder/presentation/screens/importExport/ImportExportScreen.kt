package com.romarickc.reminder.presentation.screens.importExport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
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
import com.romarickc.reminder.presentation.theme.ReminderTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun ImportExportScreen(
    viewModel: ImportExportViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit,
) {
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

    ImportExportContent(onEvent = viewModel::onEvent)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ImportExportContent(onEvent: (ImportExportEvents) -> Unit) {
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
                            fontSize = 10.sp,
                            text =
                                stringResource(R.string.import_export_data),
                        )
                    }
                }

                item {
                    TextButton(
                        onClick = {
                            onEvent(ImportExportEvents.OnSeeServerPageClick)
                        },
                        modifier = Modifier.fillMaxWidth(.8F),
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
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
                                imageVector = Icons.Rounded.CloudQueue,
                                contentDescription = "comm server",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.server),
                            )
                        }
                    }
                }

                // old method
                item {
                    ListHeader(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.old_import_export),
                        )
                    }
                }

                // import/export
                items(2) { index ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            when (index) {
                                0 -> {
                                    onEvent(ImportExportEvents.OnImportClick(1))
                                }

                                1 -> {
                                    onEvent(ImportExportEvents.OnExportClick(1))
                                }
                            }
                        },
                        colors =
                            TextButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryDim,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
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
                                        imageVector = Icons.Rounded.CloudDownload,
                                        contentDescription = "triggers Import action",
                                    )
                                }

                                1 -> {
                                    Icon(
                                        imageVector = Icons.Rounded.CloudUpload,
                                        contentDescription = "triggers Export action",
                                    )
                                }
                            }
                            when (index) {
                                0 -> {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.import_data),
                                    )
                                }

                                1 -> {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.export_data),
                                    )
                                }
                            }
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
        ImportExportContent(onEvent = {})
    }
}
