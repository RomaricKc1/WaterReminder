package com.romarickc.reminder.presentation.tiles

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders.FontSetting
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.PlatformEventSources
import androidx.wear.protolayout.material3.CardDefaults.filledVariantCardColors
import androidx.wear.protolayout.material3.CircularProgressIndicatorDefaults
import androidx.wear.protolayout.material3.TitleCardStyle
import androidx.wear.protolayout.material3.Typography.DISPLAY_LARGE
import androidx.wear.protolayout.material3.circularProgressIndicator
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.material3.titleCard
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.checkClickIdAction
import com.romarickc.reminder.commons.getCurrentIntakeTile
import com.romarickc.reminder.commons.getTargetTile
import com.romarickc.reminder.commons.getTimeLineBuilder
import com.romarickc.reminder.commons.loadLanguage
import com.romarickc.reminder.commons.openAppMod
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalHorologistApi::class)
@AndroidEntryPoint
class WaterReminderTileZen : SuspendingTileService() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources =
        Resources
            .Builder()
            .build()

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        loadLanguage(applicationContext)

        checkClickIdAction(application, requestParams, repository)

        val currentIntake: Int = getCurrentIntakeTile(repository)
        val targetVal: Int = getTargetTile(repository)

        val tile = Tile.Builder()

        val singleTileTimeline: Timeline =
            getTimeLineBuilder(
                tileLayout(
                    this,
                    requestParams.deviceConfiguration,
                    currentIntake,
                    targetVal,
                ),
            )

        tile.setFreshnessIntervalMillis(Constants.REFRESH_INTERVAL_TILE_MS)
        tile.setTileTimeline(singleTileTimeline)

        return tile.build()
    }
}

private fun tileLayout(
    context: Context,
    deviceConfiguration: DeviceParameters,
    currentIntake: Int,
    targetVal: Int,
) = materialScope(
    context = context,
    deviceConfiguration = deviceConfiguration,
    allowDynamicTheme = false,
) {
    primaryLayout(
        titleSlot = {
            text(
                context.getString(R.string.intake).layoutString,
                settings =

                    listOf(
                        FontSetting.width(60F),
                        FontSetting.weight(500),
                    ),
            )
        },
        mainSlot = {
            titleCard(
                onClick =
                    clickable(
                        id = "open_app",
                        action =
                            openAppMod(context.packageName),
                    ),
                title = {
                    text(
                        text = "$currentIntake/$targetVal".layoutString,
                        typography = DISPLAY_LARGE,
                        color = colorScheme.onPrimary,
                    )
                },
                content = {
                    circularProgressIndicator(
                        // size = expand(),
                        staticProgress = 1F * currentIntake / targetVal,
                        dynamicProgress =
                            DynamicBuilders.DynamicFloat
                                .onCondition(
                                    PlatformEventSources.isLayoutVisible(),
                                ).use(1F * currentIntake / targetVal)
                                .elseUse(0F)
                                .animate(
                                    CircularProgressIndicatorDefaults
                                        .recommendedAnimationSpec,
                                ),
                        startAngleDegrees = 200F,
                        endAngleDegrees = 520F,
                    )
                },
                height = expand(),
                colors = filledVariantCardColors(),
                style = TitleCardStyle.extraLargeTitleCardStyle(),
            )
        },
        bottomSlot = {
            textEdgeButton(
                onClick =
                    clickable(
                        id = "ID_CLICK_ADD_INTAKE",
                        action = ActionBuilders.LoadAction.Builder().build(),
                    ),
                labelContent = {
                    text(context.getString(R.string.add_intake).layoutString)
                },
            )
        },
    )
}
