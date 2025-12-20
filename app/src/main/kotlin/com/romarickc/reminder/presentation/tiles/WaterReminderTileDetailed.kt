package com.romarickc.reminder.presentation.tiles

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.MultiButtonLayout
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.Constants.DB_NOTIF_LEVEL_IDX
import com.romarickc.reminder.commons.getTimeLineBuilder
import com.romarickc.reminder.commons.loadLanguage
import com.romarickc.reminder.commons.openAppMod
import com.romarickc.reminder.commons.reSchedPeriodicWork
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
@AndroidEntryPoint
class WaterReminderTileDetailed : SuspendingTileService() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources =
        Resources
            .Builder()
            .addIdToImageMapping(
                "BUTTON_ADD_ICON_ID",
                drawableResToImageResource(R.drawable.baseline_add_20),
            ).setVersion(RESOURCES_VERSION)
            .build()

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        loadLanguage(applicationContext)
        if (requestParams.currentState.lastClickableId == "ID_CLICK_ADD_INTAKE") {
            Log.i("addintake", "add intake")
            repository.insertIntake()

            // update the worker to not send any notification that is like a minute away
            val notifPref = repository.getNotifPref(DB_NOTIF_LEVEL_IDX).firstOrNull() ?: 0
            Toast
                .makeText(
                    application,
                    application.getString(R.string.registered),
                    Toast.LENGTH_SHORT,
                ).show()

            reSchedPeriodicWork(
                context = application,
                notifPref = notifPref,
                careAboutDisabled = false,
            )
        }

        val tile = Tile.Builder()
        val singleTileTimeline: Timeline =
            getTimeLineBuilder(
                tileLayout(
                    requestParams.deviceConfiguration,
                ),
            )

        tile.setFreshnessIntervalMillis(Constants.REFRESH_INTERVAL_TILE_MS)
        tile.setTileTimeline(singleTileTimeline)
        tile.setResourcesVersion(RESOURCES_VERSION)

        return tile.build()
    }

    private suspend fun tileLayout(deviceParameters: DeviceParameters): LayoutElement {
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
                layoutInner(
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

    private fun layoutInner(
        currentIntake: Int,
        targetVal: Int,
        clickable: Clickable,
        addClickable: Clickable,
        deviceParameters: DeviceParameters,
    ) = PrimaryLayout
        .Builder(deviceParameters)
        .setResponsiveContentInsetEnabled(true)
        .setPrimaryLabelTextContent(
            Text
                .Builder(baseContext, getString(R.string.intake))
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
                        .setTextContent(
                            "$currentIntake",
                            Typography.TYPOGRAPHY_CAPTION1,
                        ).build(),
                ).addButtonContent(
                    Button
                        .Builder(baseContext, clickable)
                        .setIconContent("BUTTON_TARGET_ICON_ID")
                        .setTextContent(
                            "/$targetVal",
                            Typography.TYPOGRAPHY_CAPTION1,
                        ).build(),
                ).addButtonContent(
                    Button
                        .Builder(baseContext, addClickable)
                        .setIconContent("BUTTON_ADD_ICON_ID")
                        .build(),
                ).build(),
        ).setPrimaryChipContent(
            compactChip(deviceParameters, this.packageName),
        )

    private fun compactChip(
        deviceParameters: DeviceParameters,
        packageName: String,
    ) = CompactChip
        .Builder(
            baseContext,
            baseContext.getString(R.string.open_app),
            Clickable
                .Builder()
                .setOnClick(
                    openAppMod(packageName),
                ).build(),
            deviceParameters,
        ).setChipColors(
            ChipColors(
                argb("#1C1B1F".toColorInt()),
                argb(Color.White.toArgb()),
            ),
        ).build()
}
