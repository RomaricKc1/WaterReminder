package com.romarickc.reminder.presentation.tiles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.ProgressIndicatorColors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.EdgeContentLayout
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.getTimeLineBuilder
import com.romarickc.reminder.commons.loadLanguage
import com.romarickc.reminder.commons.setModifiers
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
@AndroidEntryPoint
class WaterReminderTileZen : SuspendingTileService() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources =
        Resources
            .Builder()
            .setVersion(RESOURCES_VERSION)
            .build()

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        loadLanguage(applicationContext)

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

        // Log.i("tile out", "tile 1: currentIntake $currentIntake targetVal $targetVal")
        return LayoutElementBuilders.Column
            .Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setModifiers(
                setModifiers(this.packageName),
            ).addContent(
                layoutInner(
                    currentIntake = currentIntake,
                    targetVal = targetVal,
                    deviceParameters,
                ).build(),
            ).build()
    }

    private fun layoutInner(
        currentIntake: Int,
        targetVal: Int,
        deviceParameters: DeviceParameters,
    ) = EdgeContentLayout
        .Builder(deviceParameters)
        .setResponsiveContentInsetEnabled(true)
        .setEdgeContent(
            CircularProgressIndicator
                .Builder()
                .setProgress(currentIntake.toFloat() / targetVal.toFloat())
                .setCircularProgressIndicatorColors(ringColor())
                .build(),
        ).setPrimaryLabelTextContent(
            Text
                .Builder(baseContext, getString(R.string.intake))
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(argb("#AECBFA".toColorInt()))
                .build(),
        ).setSecondaryLabelTextContent(
            Text
                .Builder(baseContext, "/$targetVal")
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(argb(Color.White.toArgb()))
                .build(),
        ).setContent(
            Text
                .Builder(baseContext, "$currentIntake")
                .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
                .setColor(argb(Color.White.toArgb()))
                .build(),
        )
}

private fun ringColor() =
    ProgressIndicatorColors(
        argb("#AECBFA".toColorInt()),
        argb(
            Color(1f, 1f, 1f, 0.1f)
                .toArgb(),
        ),
    )
