package com.romarickc.reminder.presentation.tile

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TimelineBuilders.Timeline
import androidx.wear.tiles.TimelineBuilders.TimelineEntry
import androidx.wear.tiles.material.CircularProgressIndicator
import androidx.wear.tiles.material.ProgressIndicatorColors
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.Typography
import androidx.wear.tiles.material.layouts.EdgeContentLayout
import com.google.android.horologist.tiles.CoroutinesTileService
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.MainActivity
import com.romarickc.reminder.presentation.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

private const val RESOURCES_VERSION = "0"

@AndroidEntryPoint
class WaterReminderTile : CoroutinesTileService() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    override suspend fun resourcesRequest(
        requestParams: ResourcesRequest
    ): Resources {
        return Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()
    }

    override suspend fun tileRequest(
        requestParams: TileRequest
    ): Tile {
        val singleTileTimeline = Timeline.Builder()
            .addTimelineEntry(
                TimelineEntry.Builder()
                    .setLayout(
                        Layout.Builder()
                            .setRoot(
                                tileLayout(
                                    requestParams.deviceParameters!!
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        return Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTimeline(singleTileTimeline)
            .setFreshnessIntervalMillis(Constants.REFRESH_INTERVAL_TILE)
            .build()
    }

    private suspend fun tileLayout(
        deviceParameters: DeviceParameters,
    ): LayoutElement {

        val now: ZonedDateTime = ZonedDateTime.now()
        val startOfDay: ZonedDateTime = now.toLocalDate().atStartOfDay(now.zone)
        val startOfDayTimestamp = startOfDay.toInstant().toEpochMilli()

        val currentIntake: Int = withContext(Dispatchers.IO) {
            repository.getCountTgtThis(startOfDayTimestamp).first()
        }

        val targetVal: Int = withContext(Dispatchers.IO) {
            repository.getTarget(1).first()
        }

        // Log.i("tile out", "currentIntake $currentIntake targetVal $targetVal")

        return Column.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(
                        Clickable.Builder()
                            .setOnClick(
                                ActionBuilders.LaunchAction.Builder()
                                    .setAndroidActivity(
                                        ActionBuilders.AndroidActivity.Builder()
                                            .setClassName(
                                                MainActivity::class.qualifiedName ?: ""
                                            )
                                            .setPackageName(this.packageName)
                                            .build()
                                    )
                                    .build()
                            ).build(),
                    )
                    .build()
            )
            .addContent(
                layout2(
                    currentIntake = currentIntake,
                    targetVal = targetVal,
                    deviceParameters,
                ).build(),
            ).build()
    }

    private fun layout2(
        currentIntake: Int,
        targetVal: Int,
        deviceParameters: DeviceParameters,
    ) = EdgeContentLayout.Builder(deviceParameters)
        .setEdgeContent(
            CircularProgressIndicator.Builder()
                .setProgress(currentIntake.toFloat() / targetVal.toFloat())
                .setCircularProgressIndicatorColors(colorThing())
                .build()
        )
        .setPrimaryLabelTextContent(
            Text.Builder(baseContext, "Intake")
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(argb(android.graphics.Color.parseColor("#AECBFA")))
                .build()
        )
        .setSecondaryLabelTextContent(
            Text.Builder(baseContext, "/$targetVal")
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(argb(Color.White.toArgb()))
                .build()
        )
        .setContent(
            Text.Builder(baseContext, "$currentIntake")
                .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
                .setColor(argb(Color.White.toArgb()))
                .build()
        )
}

private fun colorThing() = ProgressIndicatorColors(
    argb(android.graphics.Color.parseColor("#AECBFA")),
    argb(Color(1f, 1f, 1f, 0.1f).toArgb())
)