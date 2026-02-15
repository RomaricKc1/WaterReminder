package com.romarickc.reminder.presentation.screens.intakeHistory

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants.MONTHS_CNT
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.WaterIntakeData
import com.romarickc.reminder.commons.averageToMonth
import com.romarickc.reminder.commons.listIntakes
import com.romarickc.reminder.commons.mapHeight
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.ReminderTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun MonthsGraphScreen(
    viewModel: IntakeHistoryViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val currentMonthIntakes = viewModel.intakes.collectAsState(initial = listOf()).value

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

    MonthsGraphContent(waterIntakeList = currentMonthIntakes)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MonthsGraphContent(waterIntakeList: List<WaterIntake>) {
    val waterIntakeData = WaterIntakeData(waterIntakeList)
    val monthIntakeData = waterIntakeData.getMonthIntakeData()

    Log.i("graph data", "$monthIntakeData")

    AppScaffold {
        val listState = rememberTransformingLazyColumnState()

        ScreenScaffold(
            scrollState = listState,
            contentPadding =
                rememberResponsiveColumnPadding(
                    first = ColumnItemType.IconButton,
                    last = ColumnItemType.Button,
                ),
            edgeButton = {
            },
        ) { contentPadding ->
            TransformingLazyColumn(
                state = listState,
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight(),
            ) {
                item {
                    Log.i("graph", "this year")
                    GraphMonths(monthIntakeData)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun GraphMonths(data: Map<Int, Int>) {
    val last12Months = stringResource(R.string.last_12_months)
    val maxValue = data.values.maxOrNull() ?: 0

    val avgToDate = averageToMonth(data)
    Log.i("intakes day", "average to month: $avgToDate")

    val monthsViewStr = stringResource(R.string.months_view_graph)
    val intakeText =
        if (maxValue <= 1) {
            stringResource(R.string.top_intake)
                .format(maxValue, avgToDate.toInt())
        } else {
            stringResource(R.string.top_intakes)
                .format(maxValue, avgToDate.toInt())
        }

    Canvas(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        val height = 50f
        val height2 = -50f
        val maxWidth = size.maxDimension

        drawContext.canvas.nativeCanvas.drawText(
            last12Months,
            center.x,
            center.y - 90,
            paint2,
        )

        // Draw the X-axis
        drawLine(
            start = Offset(0f, height2 - 20),
            end = Offset(maxWidth, height2 - 20),
            color = Color.Blue,
            strokeWidth = 1.5f,
        )

        // avg line
        var actualHeight =
            mapHeight(
                avgToDate,
                0f,
                maxValue.toFloat(),
                0f,
                50f,
            )

        var offsetStart = Offset(0f, height - actualHeight.times(2))
        var offsetEnd = Offset(maxWidth, height - actualHeight.times(2))

        // Draw the avg X-axis
        drawLine(start = offsetStart, end = offsetEnd, color = Color.Green, strokeWidth = 1.5f)

        // Calculate the spacing between dots, for 12 months
        val numberOfPoints = 12f
        val dotSpacing = (maxWidth / numberOfPoints)
        // Log.i("graph", "spacing $dotSpacing")

        // Draw dots along the X-axis
        for (i in 1..MONTHS_CNT) {
            drawCircle(
                brush = SolidColor(Color.White),
                radius = 4f,
                style = Stroke(width = .97f, cap = StrokeCap.Round), // Fill,
                center = Offset(i * dotSpacing, height),
            )
        }

        // Draw data
        for (i in 1..MONTHS_CNT) {
            offsetStart = Offset(dotSpacing * i, height)
            data[i]?.let {
                actualHeight =
                    mapHeight(
                        it.toFloat(),
                        0f,
                        maxValue.toFloat(),
                        0f,
                        50f,
                    )
            }

            offsetEnd = Offset(dotSpacing * i, height - (actualHeight.times(2)))

            drawLine(color = Color.White, start = offsetStart, end = offsetEnd, strokeWidth = 4.0f)

            // Draw labels
            if (i == 1) {
                drawContext.canvas.nativeCanvas.drawText(
                    monthNames[0],
                    dotSpacing * (i),
                    73f,
                    paint,
                )
            }
            if (i % 5 == 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    monthNames[i - 1],
                    dotSpacing * (i),
                    73f,
                    paint,
                )
            }
        }
        // Print max value
        drawContext.canvas.nativeCanvas.drawText(
            intakeText,
            center.x,
            center.y + 130,
            paint2,
        )
        drawContext.canvas.nativeCanvas.drawText(
            monthsViewStr,
            center.x,
            center.y + 150,
            paint3,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewMonthsGraph() {
    ReminderTheme {
        MonthsGraphContent(
            waterIntakeList = listIntakes,
        )
    }
}
