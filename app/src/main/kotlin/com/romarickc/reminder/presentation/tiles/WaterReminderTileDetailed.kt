package com.romarickc.reminder.presentation.tiles

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.FontSetting
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material3.ButtonColors
import androidx.wear.protolayout.material3.ButtonGroupDefaults.DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.buttonGroup
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.material3.textEdgeButton
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
class WaterReminderTileDetailed : SuspendingTileService() {
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
                    intakeElm = UiElm(name = "intake", value = "$currentIntake/$targetVal"),
                    incElm = UiElm(name = "add", value = "+"),
                    decElm = UiElm(name = "rm", value = "-"),
                ),
            )

        tile.setFreshnessIntervalMillis(Constants.REFRESH_INTERVAL_TILE_MS)
        tile.setTileTimeline(singleTileTimeline)

        return tile.build()
    }
}

data class UiElm(
    val value: String,
    val name: String,
)

private fun tileLayout(
    context: Context,
    deviceConfiguration: DeviceParameters,
    incElm: UiElm,
    decElm: UiElm,
    intakeElm: UiElm,
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
            Column
                .Builder()
                .apply {
                    setWidth(expand())
                    setHeight(expand())
                    addContent(DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS)
                    addContent(
                        buttonGroup {
                            buttonGroupItem { uiElmBtn(intakeElm, clickable = null) }
                        },
                    )
                    addContent(DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS)
                    addContent(
                        buttonGroup {
                            buttonGroupItem {
                                uiElmBtn(
                                    decElm,
                                    clickable =
                                        clickable(
                                            id = "ID_CLICK_RM_INTAKE",
                                            action = ActionBuilders.LoadAction.Builder().build(),
                                        ),
                                )
                            }
                            buttonGroupItem {
                                uiElmBtn(
                                    incElm,
                                    clickable =
                                        clickable(
                                            id = "ID_CLICK_ADD_INTAKE",
                                            action = ActionBuilders.LoadAction.Builder().build(),
                                        ),
                                )
                            }
                        },
                    )
                }.build()
        },
        bottomSlot = {
            textEdgeButton(
                onClick =
                    clickable(
                        id = "open_app",
                        action =
                            openAppMod(context.packageName),
                    ),
                labelContent = {
                    text(context.getString(R.string.open_app).layoutString)
                },
            )
        },
    )
}

private fun MaterialScope.uiElmBtn(
    elm: UiElm,
    clickable: Clickable?,
): LayoutElement {
    val emptyClick = clickable()

    val colors = buttonColors(elm.name)

    return textButton(
        onClick =
            clickable ?: emptyClick,
        labelContent = {
            text(
                text = elm.value.layoutString,
                color = colors.labelColor,
                settings =
                    listOf(FontSetting.width(60F), FontSetting.weight(500)),
            )
        },
        width = expand(),
        height = expand(),
        contentPadding = padding(horizontal = 4F, vertical = 2F),
        colors = colors,
    )
}

private fun MaterialScope.buttonColors(name: String): ButtonColors =
    when (name) {
        "add" -> {
            ButtonColors(
                labelColor = colorScheme.onSecondary,
                containerColor = colorScheme.secondaryDim,
            )
        }

        "rm" -> {
            ButtonColors(
                labelColor = colorScheme.onSecondary,
                containerColor = colorScheme.secondaryDim,
            )
        }

        else -> {
            ButtonColors(
                labelColor = colorScheme.onPrimary,
                containerColor = colorScheme.primaryDim,
            )
        }
    }
