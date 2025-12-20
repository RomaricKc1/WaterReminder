package com.romarickc.reminder.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.R
import com.romarickc.reminder.presentation.theme.ReminderTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun PermissionDialog(
    showThis: Boolean,
    shouldShowRationale: Boolean,
    onOkClick: () -> Unit,
    onSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScalingLazyListState()

    Dialog(
        showDialog = showThis,
        onDismissRequest = onDismiss,
        scrollState = scrollState,
    ) {
        Alert(
            scrollState = scrollState,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            contentPadding =
                PaddingValues(start = 10.dp, end = 10.dp, top = 25.dp, bottom = 25.dp),
            icon = {
                Icon(
                    Icons.Rounded.Notifications,
                    contentDescription = "notifIco",
                    modifier =
                        Modifier
                            .size(30.dp)
                            .wrapContentSize(align = Alignment.Center),
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.permission_required),
                    textAlign = TextAlign.Center,
                )
            },
            message = {
                Text(
                    text =
                        if (shouldShowRationale) {
                            stringResource(R.string.explain_notif_importance)
                        } else {
                            stringResource(R.string.user_perm_deny)
                        },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            },
        ) {
            if (shouldShowRationale) {
                item {
                    Chip(
                        label = {
                            Text(
                                stringResource(R.string.allow),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        onClick = onOkClick,
                        modifier = Modifier.fillMaxWidth(.8F),
                        colors = ChipDefaults.primaryChipColors(),
                    )
                }
                item {
                    Chip(
                        label = {
                            Text(
                                stringResource(R.string.deny),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(.8F),
                        colors = ChipDefaults.secondaryChipColors(),
                    )
                }
            } else {
                item {
                    Chip(
                        label = {
                            Text(
                                stringResource(R.string.navigate_to_settings),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        onClick = onSettings,
                        modifier = Modifier.fillMaxWidth(.8F),
                        colors = ChipDefaults.primaryChipColors(),
                    )
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ReminderTheme {
        PermissionDialog(
            showThis = true,
            shouldShowRationale = false,
            onOkClick = {},
            onDismiss = {},
            onSettings = {},
        )
    }
}
