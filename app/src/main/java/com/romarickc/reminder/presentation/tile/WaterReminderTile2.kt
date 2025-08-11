package com.romarickc.reminder.presentation.tile

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.MultiButtonLayout
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.romarickc.reminder.R
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.MainActivity
import com.romarickc.reminder.presentation.utils.Constants
import com.romarickc.reminder.presentation.utils.WaterReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
@AndroidEntryPoint
class WaterReminderTile2 : SuspendingTileService() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources =
        ResourceBuilders.Resources
            .Builder()
            .addIdToImageMapping(
                "BUTTON_ADD_ICON_ID",
                drawableResToImageResource(R.drawable.baseline_add_20),
            ).setVersion(RESOURCES_VERSION)
            .build()

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        if (requestParams.currentState.lastClickableId == "ID_CLICK_ADD_INTAKE") {
            Log.i("addintake", "add intake")
            repository.insertIntake()

            // update the worker to not send any notification that is like a minute away
            val notifPref = repository.getNotifPref(1).firstOrNull() ?: 0
            Toast.makeText(application, "Registered", Toast.LENGTH_SHORT).show()
            when (notifPref) {
                1 -> {
                    // the 3 hours interval
                    updatePeriodicWorkInterval(3)
                }

                2 -> {
                    // notification disabled, so I don't care
                }

                0 -> {
                    // the 1 hour interval
                    updatePeriodicWorkInterval(1)
                }
            }
        }

        val tile = Tile.Builder()

        val singleTileTimeline =
            Timeline
                .Builder()
                .addTimelineEntry(
                    TimelineEntry
                        .Builder()
                        .setLayout(
                            LayoutElementBuilders.Layout
                                .Builder()
                                .setRoot(
                                    tileLayout(
                                        requestParams.deviceConfiguration,
                                    ),
                                ).build(),
                        ).build(),
                ).build()
        tile.setFreshnessIntervalMillis(Constants.REFRESH_INTERVAL_TILE_MS)
        tile.setTileTimeline(singleTileTimeline)
        tile.setResourcesVersion(RESOURCES_VERSION)
        return tile
            .build()
    }

    private suspend fun tileLayout(deviceParameters: DeviceParametersBuilders.DeviceParameters): LayoutElementBuilders.LayoutElement {
        val now: ZonedDateTime = ZonedDateTime.now()
        val startOfDay: ZonedDateTime = now.toLocalDate().atStartOfDay(now.zone)
        val startOfDayTimestamp = startOfDay.toInstant().toEpochMilli()

        val currentIntake: Int =
            withContext(Dispatchers.IO) {
                repository.getCountTgtThis(startOfDayTimestamp).first()
            }

        val targetVal: Int =
            withContext(Dispatchers.IO) {
                repository.getTarget(1).first()
            }

        // Log.i("tile out", "tile 2: currentIntake $currentIntake targetVal $targetVal")

        return LayoutElementBuilders.Column
            .Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                layout3(
                    currentIntake = currentIntake,
                    targetVal = targetVal,
                    Clickable
                        .Builder()
                        .setOnClick(
                            ActionBuilders.LoadAction.Builder().build(),
                        ).build(),
                    Clickable
                        .Builder()
                        .setId("ID_CLICK_ADD_INTAKE")
                        .setOnClick(
                            ActionBuilders.LoadAction.Builder().build(),
                        ).build(),
                    deviceParameters,
                ).build(),
            ).build()
    }

    private fun updatePeriodicWorkInterval(interval: Long) {
        Log.i("register intake notif", "called")
        var workInfos =
            WorkManager
                .getInstance(application)
                .getWorkInfosByTag("water_reminder_periodic_work")
                .get()

        Log.i("worker settings", "current cancelling and recreating $workInfos")
        val workRequest =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(
                interval,
                TimeUnit.HOURS,
            ).setInitialDelay(interval, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder().setRequiresBatteryNotLow(true).build(),
                ).addTag("water_reminder_periodic_work") // set the tag here
                .build()

        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            "water_reminder_periodic_work",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest,
        )
        workInfos =
            WorkManager
                .getInstance(application)
                .getWorkInfosByTag("water_reminder_periodic_work")
                .get()

        Log.i("worker register intake", "new one -> $workInfos")
    }

    private fun layout3(
        currentIntake: Int,
        targetVal: Int,
        clickable: Clickable,
        addClickable: Clickable,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
    ) = PrimaryLayout
        .Builder(deviceParameters)
        .setResponsiveContentInsetEnabled(true)
        .setPrimaryLabelTextContent(
            Text
                .Builder(baseContext, "Intake")
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(argb("#AECBFA".toColorInt()))
                .build(),
        ).setContent(
            MultiButtonLayout
                .Builder()
                .addButtonContent(
                    Button
                        .Builder(baseContext, clickable)
                        .setIconContent("BUTTON_INTAKE_ICON_ID")
                        .setTextContent("$currentIntake", Typography.TYPOGRAPHY_CAPTION1)
                        .build(),
                ).addButtonContent(
                    Button
                        .Builder(baseContext, clickable)
                        .setIconContent("BUTTON_TARGET_ICON_ID")
                        .setTextContent("/$targetVal", Typography.TYPOGRAPHY_CAPTION1)
                        .build(),
                ).addButtonContent(
                    Button
                        .Builder(baseContext, addClickable)
                        .setIconContent("BUTTON_ADD_ICON_ID")
                        .build(),
                ).build(),
        ).setPrimaryChipContent(
            CompactChip
                .Builder(
                    baseContext,
                    "Open app",
                    Clickable
                        .Builder()
                        .setOnClick(
                            ActionBuilders.LaunchAction
                                .Builder()
                                .setAndroidActivity(
                                    ActionBuilders.AndroidActivity
                                        .Builder()
                                        .setClassName(MainActivity::class.qualifiedName ?: "")
                                        .setPackageName(this.packageName)
                                        .build(),
                                ).build(),
                        ).build(),
                    deviceParameters,
                ).setChipColors(
                    ChipColors(
                        argb("#1C1B1F".toColorInt()),
                        argb(Color.White.toArgb()),
                    ),
                ).build(),
        )
}
