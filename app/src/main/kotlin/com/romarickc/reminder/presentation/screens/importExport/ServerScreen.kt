package com.romarickc.reminder.presentation.screens.importExport

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CircularProgressIndicator
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
import com.romarickc.reminder.commons.AnyState
import com.romarickc.reminder.commons.ServerPing
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.presentation.theme.ReminderTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun ServerScreen(
    viewModel: ImportExportViewModel = hiltViewModel(),
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
    val qrBitmap by viewModel.qrBitmap.collectAsState()

    ServerContent(
        onEvent = viewModel::onEvent,
        storedSAddress = viewModel.storedSAddress,
        qrBitmap = qrBitmap,
        stateServerComm = viewModel.serverPingState,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ServerContent(
    onEvent: (ImportExportEvents) -> Unit,
    stateServerComm: AnyState<ServerPing>,
    storedSAddress: String,
    qrBitmap: Bitmap?,
) {
    stateServerComm.data?.let { data ->
        DispServerComm(onEvent, storedSAddress, qrBitmap, data)
    }

    if (stateServerComm.data == null) {
        // reload data
        var serverAddress by remember { mutableStateOf(storedSAddress) }
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
                                fontSize = 8.sp,
                                text =
                                    stringResource(R.string.import_export_data) + "\n" +
                                        stringResource(R.string.comm_server),
                            )
                        }
                    }

                    // address info
                    item {
                        ServerAddressInfo(
                            serverAddress,
                            SurfaceTransformation(transformationSpec),
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        )
                    }

                    // handle loading
                    item {
                        HandleLoading(stateServerComm)
                    }

                    // handle error
                    item {
                        HandleError(stateServerComm)
                    }

                    // ping server
                    item {
                        TextButton(
                            onClick = {
                                onEvent(ImportExportEvents.OnServerPing)
                            },
                            modifier = Modifier.fillMaxWidth(.8F),
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
                                Icon(
                                    imageVector = Icons.Rounded.CloudQueue,
                                    contentDescription = "comm server",
                                )
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = stringResource(R.string.ping),
                                )
                            }
                        }
                    }

                    // settings
                    item {
                        ListHeader(
                            modifier =
                                Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Center,
                                text =
                                    stringResource(R.string.settings),
                            )
                        }
                    }

                    // address input
                    item {
                        serverAddress = addressInput(serverAddress)
                    }

                    // chg address
                    item {
                        ServerAddressUpdate(onEvent, serverAddress)
                    }
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun <T> HandleLoading(state: AnyState<T>) {
    if (state.loading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun <T> HandleError(state: AnyState<T>) {
    state.error?.let { error ->
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            },
            colors =
                TextButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
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
                    imageVector = Icons.Rounded.Error,
                    contentDescription = "error loading server",
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = error,
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun DispServerComm(
    onEvent: (ImportExportEvents) -> Unit,
    storedSAddress: String,
    qrBitmap: Bitmap?,
    data: ServerPing,
) {
    var serverAddress by remember { mutableStateOf(storedSAddress) }
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
                // current address
                item {
                    ServerAddressInfo(
                        serverAddress,
                        SurfaceTransformation(transformationSpec),
                        Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                    )
                }

                // read data line
                item {
                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        text = "Server response\n${data.response}\n",
                    )
                }

                // ping server
                item {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(.8F),
                        onClick = {
                            onEvent(ImportExportEvents.OnServerPing)
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
                            Icon(
                                imageVector = Icons.Rounded.CloudQueue,
                                contentDescription = "comm server",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.ping),
                            )
                        }
                    }
                }

                // qrcode header
                item {
                    qrBitmap?.let {
                        ListHeader(
                            modifier =
                                Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Center,
                                text = "Qr Code",
                            )
                        }
                    }
                }

                // qrcode display
                item {
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR",
                            modifier = Modifier.size(100.dp),
                        )
                    }
                }

                // export header
                item {
                    ListHeader(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text =
                                stringResource(R.string.export_data),
                        )
                    }
                }

                // export to server and qrcode
                item {
                    TextButton(
                        onClick = {
                            onEvent(ImportExportEvents.OnExportToServer)
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
                                imageVector = Icons.Rounded.Upload,
                                contentDescription = "export data to server",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.export_data),
                            )
                        }
                    }
                }

                // import header
                item {
                    ListHeader(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text =
                                stringResource(R.string.import_data),
                        )
                    }
                }

                // import from server
                item {
                    TextButton(
                        onClick = {
                            onEvent(ImportExportEvents.OnLoadServerIntakeData)
                        },
                        modifier = Modifier.fillMaxWidth(.8F),
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
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = "import data from server",
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.import_data),
                            )
                        }
                    }
                }

                // settings
                item {
                    ListHeader(
                        modifier =
                            Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            text =
                                stringResource(R.string.settings),
                        )
                    }
                }

                // address input
                item {
                    serverAddress = addressInput(serverAddress)
                }

                // chg address
                item {
                    ServerAddressUpdate(onEvent, serverAddress)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun addressInput(address: String): String {
    var address by remember { mutableStateOf(address) }
    OutlinedTextField(
        value = address,
        onValueChange = {
            address = it
        },
        modifier = Modifier.padding(bottom = 4.dp),
        label = {
            Text(text = stringResource(R.string.enter_address))
        },
    )
    return address
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ServerAddressUpdate(
    onEvent: (ImportExportEvents) -> Unit,
    address: String,
) {
    TextButton(
        onClick = {
            onEvent(ImportExportEvents.OnChgServerAddress(address))
        },
        modifier = Modifier.fillMaxWidth(.8F),
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
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "changes server address",
            )
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(R.string.chg_address),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ServerAddressInfo(
    address: String,
    transformation: SurfaceTransformation,
    modifier: Modifier,
) {
    ListHeader(
        modifier = modifier,
        transformation = transformation,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            text =
                stringResource(R.string.server_info)
                    .format(address),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewServer() {
    ReminderTheme {
        ServerContent(
            onEvent = {},
            storedSAddress = "server.local",
            stateServerComm = AnyState(),
            qrBitmap = createBitmap(100, 100),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewDispServer() {
    ReminderTheme {
        DispServerComm(
            onEvent = {},
            storedSAddress = "server.local",
            data = ServerPing("nothing"),
            qrBitmap = createBitmap(100, 100),
        )
    }
}
