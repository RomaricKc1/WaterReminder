package com.romarickc.reminder.presentation.screens.intakeHistory

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.WaterIntakeData
import com.romarickc.reminder.commons.averageToDay
import com.romarickc.reminder.commons.listIntakes
import com.romarickc.reminder.commons.mapHeight
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.ReminderTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun DaysGraphScreen(
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
                else -> Unit
            }
        }
    })

    DaysGraphContent(waterIntakeList = currentMonthIntakes)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun DaysGraphContent(waterIntakeList: List<WaterIntake>) {
    val waterIntakeData = WaterIntakeData(waterIntakeList)
    val dayIntakeData = waterIntakeData.getDayIntakeData()

    // Log.i("graph data", "$dayIntakeData")
    // var theText by remember { mutableStateOf("") }
    var current by remember { mutableIntStateOf(0) }

    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            item {
                if (current == 0) {
                    GraphDays(dayIntakeData)
                    Log.i("graph", "this month")
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun GraphDays(data: Map<Int, Int>) {
    val maxValue = data.values.maxOrNull() ?: 0
    val days = data.size

    val lastXDays = stringResource(R.string.last_x_days).format(days)
    val daysViewStr = stringResource(R.string.days_view_graph)
    val intakeText =
        if (maxValue <= 1) {
            stringResource(R.string.top_intake)
                .format(maxValue)
        } else {
            stringResource(R.string.top_intakes)
                .format(maxValue)
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
            lastXDays,
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

        // Draw the avg X-axis
        val avgToDate = averageToDay(data)
        Log.i("intakes day", "average to day: $avgToDate")

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

        drawLine(start = offsetStart, end = offsetEnd, color = Color.Green, strokeWidth = 1.5f)
        // Calculate the spacing between dots
        val numberOfPoints = days.toFloat()
        val dotSpacing = (maxWidth / numberOfPoints)
        // Log.i("graph", "spacing $dotSpacing")

        // Draw dots along the X-axis
        for (i in 1..days) {
            drawCircle(
                brush = SolidColor(Color.White),
                radius = 4f,
                style = Stroke(width = .97f, cap = StrokeCap.Round), // Fill,
                center = Offset(i * dotSpacing, height),
            )
        }

        // Draw data
        for (i in 1..days) {
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

            offsetStart = Offset(dotSpacing * i, height)
            offsetEnd = Offset(dotSpacing * i, height - (actualHeight.times(2)))

            drawLine(color = Color.White, start = offsetStart, end = offsetEnd, strokeWidth = 4.0f)

            // Draw labels
            if (i % 5 == 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    i.toString(),
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
            daysViewStr,
            center.x,
            center.y + 150,
            paint3,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewDaysGraph() {
    ReminderTheme {
        DaysGraphContent(
            waterIntakeList = listIntakes,
        )
    }
}
