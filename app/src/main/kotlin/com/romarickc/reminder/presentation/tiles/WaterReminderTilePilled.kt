package com.romarickc.reminder.presentation.tiles

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.TEXT_ALIGN_END
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.PlatformEventSources
import androidx.wear.protolayout.material3.ButtonColors
import androidx.wear.protolayout.material3.CardDefaults.filledTonalCardColors
import androidx.wear.protolayout.material3.CircularProgressIndicatorDefaults
import androidx.wear.protolayout.material3.GraphicDataCardDefaults.constructGraphic
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography.DISPLAY_SMALL
import androidx.wear.protolayout.material3.circularProgressIndicator
import androidx.wear.protolayout.material3.graphicDataCard
import androidx.wear.protolayout.material3.icon
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.modifiers.padding
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
class WaterReminderTilePilled : SuspendingTileService() {
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
                    currentIntake = currentIntake,
                    targetVal = targetVal,
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
    val colors = buttonColors()

    primaryLayout(
        mainSlot = {
            graphicDataCard(
                onClick =
                    clickable(
                        id = "open_app",
                        action =
                            openAppMod(context.packageName),
                    ),
                height = expand(),
                colors = filledTonalCardColors(),
                title = {
                    text(
                        text = "$currentIntake of $targetVal".layoutString,
                        typography = DISPLAY_SMALL,
                        alignment = TEXT_ALIGN_END,
                    )
                },
                content = {
                    textButton(
                        onClick =
                            clickable(
                                id = "ID_CLICK_ADD_INTAKE",
                                action = ActionBuilders.LoadAction.Builder().build(),
                            ),
                        labelContent = {
                            text(
                                text = "Add".layoutString,
                                color = colors.labelColor,
                                settings =
                                    listOf(
                                        LayoutElementBuilders.FontSetting.width(60F),
                                        LayoutElementBuilders.FontSetting.weight(500),
                                    ),
                            )
                        },
                        width = expand(),
                        height = expand(),
                        contentPadding = padding(horizontal = 4F, vertical = 2F),
                        colors = colors,
                    )
                },
                horizontalAlignment = LayoutElementBuilders.HORIZONTAL_ALIGN_END,
                graphic = {
                    constructGraphic(
                        mainContent = {
                            circularProgressIndicator(
                                size = expand(),
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
                        iconContent = {
                            icon(
                                context.resources.getResourceName(
                                    R.drawable.outline_water_full_24,
                                ),
                            )
                        },
                    )
                },
            )
        },
    )
}

private fun MaterialScope.buttonColors(): ButtonColors =
    ButtonColors(
        labelColor = colorScheme.onSecondary,
        containerColor = colorScheme.secondaryDim,
    )
