package com.romarickc.reminder.presentation.screen.intakeH

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.presentation.theme.ReminderTheme
import com.romarickc.reminder.presentation.utils.UiEvent
import java.util.Calendar
import java.util.Date

@Suppress("ktlint:standard:function-naming")
@Composable
fun SeeIntakeGraphScreen(
    viewModel: IntakeHistoryViewModel = hiltViewModel(),
    onPopBackStack: (UiEvent.PopBackStack) -> Unit,
) {
    val currentMonthIntakes = viewModel.intakes.collectAsState(initial = listOf()).value
    // val currentMonthIntakes = viewModel.monthIntakes.collectAsState(initial = listOf()).value
    // Log.i("intakes", "$waterIntakeList, \n$currentMonthIntakes")

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

    SeeGraphContent(waterIntakeList = currentMonthIntakes)
}

fun mapH(
    value: Float,
    min: Float,
    max: Float,
): Float = (value - min) / (max - min) * (max - min) + min

fun mapH2(
    value: Float,
    minValue: Float,
    maxValue: Float,
    minDrawHeight: Float,
    maxDrawHeight: Float,
): Float = ((value - minValue) / (maxValue - minValue)) * (maxDrawHeight - minDrawHeight) + minDrawHeight

@Suppress("ktlint:standard:function-naming")
@Composable
fun GraphDays(data: Map<Int, Int>) {
    Canvas(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        val height = 50f
        val height2 = -50f
        val maxWidth = size.maxDimension

        val maxValue = data.values.maxOrNull() ?: 0
        val days = data.size

        /*Log.i(
            "pain",
            "dimensions: $size, ${size.height}, ${size.width}, ${size.maxDimension}, ${size.minDimension}"
        )*/

        // Log.i("maxval", "$maxValue")

        val paint =
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 20f
                color = Color.White.toArgb()
            }
        val paint2 =
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 25f
                color = Color.White.toArgb()
            }
        val paint3 =
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 21f
                color = Color.White.toArgb()
            }
        drawContext.canvas.nativeCanvas.drawText("Last $days days", center.x, center.y - 90, paint2)
        // Draw the X-axis
        drawLine(
            start = Offset(0f, height2 - 20),
            end = Offset(maxWidth, height2 - 20),
            color = Color.Blue,
            strokeWidth = 1.5f,
        )

        // Draw the avg X-axis
        val avgtodate = averageToDay(data)
        Log.i("intakes day", "average to day: $avgtodate")
        drawLine(
            start =
                Offset(
                    0f,
                    height -
                        (
                            mapH(
                                mapH2(
                                    avgtodate,
                                    0f,
                                    maxValue.toFloat(),
                                    0f,
                                    50f,
                                ),
                                height2,
                                height,
                            )
                        ).times(
                            2,
                        ),
                ),
            end =
                Offset(
                    maxWidth,
                    height -
                        (
                            mapH(
                                mapH2(
                                    avgtodate,
                                    0f,
                                    maxValue.toFloat(),
                                    0f,
                                    50f,
                                ),
                                height2,
                                height,
                            )
                        ).times(
                            2,
                        ),
                ),
            color = Color.Green,
            strokeWidth = 1.5f,
        )
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
            drawLine(
                color = Color.White,
                start = Offset(dotSpacing * i, height),
                end =
                    Offset(
                        dotSpacing * i,
                        height - (
                            (
                                data[i]?.let {
                                    mapH(
                                        mapH2(
                                            it.toFloat(),
                                            0f,
                                            maxValue.toFloat(),
                                            0f,
                                            50f,
                                        ),
                                        height2,
                                        height,
                                    )
                                }
                            )?.times(
                                2,
                            )!!
                        ),
                    ),
                strokeWidth = 4.0f,
            )

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
        val intakeText =
            if (maxValue <= 1) {
                "TOP: $maxValue intake"
            } else {
                "TOP: $maxValue intakes"
            }
        drawContext.canvas.nativeCanvas.drawText(
            intakeText,
            center.x,
            center.y + 130,
            paint2,
        )
        drawContext.canvas.nativeCanvas.drawText(
            "Click center for Months",
            center.x,
            center.y + 150,
            paint3,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun GraphMonths(data: Map<Int, Int>) {
    Canvas(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        val height = 50f
        val height2 = -50f
        val maxWidth = size.maxDimension

        val maxValue = data.values.maxOrNull() ?: 0
        val months = 12

        val paint =
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 20f
                color = Color.White.toArgb()
            }
        val paint2 =
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 25f
                color = Color.White.toArgb()
            }
        val paint3 =
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 21f
                color = Color.White.toArgb()
            }
        drawContext.canvas.nativeCanvas.drawText("Last 12 Months", center.x, center.y - 90, paint2)
        // Draw the X-axis
        drawLine(
            start = Offset(0f, height2 - 20),
            end = Offset(maxWidth, height2 - 20),
            color = Color.Blue,
            strokeWidth = 1.5f,
        )

        // Draw the avg X-axis
        val avgtodate = averageToMonth(data)
        Log.i("intakes day", "average to month: $avgtodate")
        drawLine(
            start =
                Offset(
                    0f,
                    height -
                        (
                            mapH(
                                mapH2(
                                    avgtodate,
                                    0f,
                                    maxValue.toFloat(),
                                    0f,
                                    50f,
                                ),
                                height2,
                                height,
                            )
                        ).times(
                            2,
                        ),
                ),
            end =
                Offset(
                    maxWidth,
                    height -
                        (
                            mapH(
                                mapH2(
                                    avgtodate,
                                    0f,
                                    maxValue.toFloat(),
                                    0f,
                                    50f,
                                ),
                                height2,
                                height,
                            )
                        ).times(
                            2,
                        ),
                ),
            color = Color.Green,
            strokeWidth = 1.5f,
        )
        // Calculate the spacing between dots, for 12 months
        val numberOfPoints = 12f
        val dotSpacing = (maxWidth / numberOfPoints)
        // Log.i("graph", "spacing $dotSpacing")

        // Draw dots along the X-axis
        for (i in 1..months) {
            drawCircle(
                brush = SolidColor(Color.White),
                radius = 4f,
                style = Stroke(width = .97f, cap = StrokeCap.Round), // Fill,
                center = Offset(i * dotSpacing, height),
            )
        }
        val monthNames =
            arrayOf(
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sept",
                "Oct",
                "Nov",
                "Dec",
            )

        // Draw data
        for (i in 1..months) {
            drawLine(
                color = Color.White,
                start = Offset(dotSpacing * i, height),
                end =
                    Offset(
                        dotSpacing * i,
                        height - (
                            (
                                data[i]?.let {
                                    mapH(
                                        mapH2(
                                            it.toFloat(),
                                            0f,
                                            maxValue.toFloat(),
                                            0f,
                                            50f,
                                        ),
                                        height2,
                                        height,
                                    )
                                }
                            )?.times(
                                2,
                            )!!
                        ),
                    ),
                strokeWidth = 4.0f,
            )

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
        val intakeText =
            if (maxValue <= 1) {
                "TOP: $maxValue intake"
            } else {
                "TOP: $maxValue intakes"
            }
        drawContext.canvas.nativeCanvas.drawText(
            intakeText,
            center.x,
            center.y + 130,
            paint2,
        )
        drawContext.canvas.nativeCanvas.drawText(
            "Click center for Days",
            center.x,
            center.y + 150,
            paint3,
        )
    }
}

fun averageToDay(intakeData: Map<Int, Int>): Float {
    val calendar = Calendar.getInstance()
    val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    var totalIntake = 0
    for (dayOfMonth in 1..currentDayOfMonth) {
        totalIntake += intakeData.getOrDefault(dayOfMonth, 0)
    }
    return totalIntake.toFloat() / currentDayOfMonth
}

fun averageToMonth(intakeData: Map<Int, Int>): Float {
    val calendar = Calendar.getInstance()
    val currentMonth =
        calendar.get(Calendar.MONTH) + 1 // Adding 1 since month is 0-indexed in Calendar
    Log.i("data avg", "current month: $currentMonth")
    var totalIntake = 0
    for (month in 1..currentMonth) {
        totalIntake += intakeData.getOrDefault(month, 0)
    }
    return totalIntake.toFloat() / currentMonth
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SeeGraphContent(waterIntakeList: List<WaterIntake>) {
    val waterIntakeData = WaterIntakeData(waterIntakeList)
    val dayIntakeData = waterIntakeData.getDayIntakeData()
    val monthIntakeData = waterIntakeData.getMonthIntakeData()

    Log.i("graphdata", "$monthIntakeData, $dayIntakeData")
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
                } else {
                    Log.i("graph", "this year")
                    GraphMonths(monthIntakeData)
                }
            }
            item {
                Text(
                    text = "                            ",
                    modifier =
                        Modifier
                            .clickable {
                                current =
                                    if (current == 1) {
                                        0
                                    } else {
                                        1
                                    }
                            },
                )
            }
        }
    }
}

class WaterIntakeData(
    waterIntakeList: List<WaterIntake>,
) {
    private val dayIntakeData = mutableMapOf<Int, Int>()
    private val monthIntakeData = mutableMapOf<Int, Int>()

    init {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        // days of the month
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (dayOfMonth in 1..maxDaysInMonth) {
            dayIntakeData[dayOfMonth] = 0
        }
        waterIntakeList.forEach {
            calendar.time = it.timestamp?.let { it1 -> Date(it1) }!!
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            if (year == currentYear && month == currentMonth) {
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                dayIntakeData[dayOfMonth] = dayIntakeData.getOrDefault(dayOfMonth, 0) + 1
            }
        }

        // month of the year
        for (month in 1..12) {
            monthIntakeData[month] = 0
        }
        waterIntakeList.forEach {
            calendar.time = it.timestamp?.let { it1 -> Date(it1) }!!
            val year = calendar.get(Calendar.YEAR)
            if (year == currentYear) {
                val month = calendar.get(Calendar.MONTH) + 1
                monthIntakeData[month] = monthIntakeData.getOrDefault(month, 0) + 1
            }
        }
    }

    fun getDayIntakeData(): Map<Int, Int> = dayIntakeData

    fun getMonthIntakeData(): Map<Int, Int> = monthIntakeData
}

@Suppress("ktlint:standard:function-naming")
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewGraph() {
    ReminderTheme {
        SeeGraphContent(
            waterIntakeList =
                listOf(
                    WaterIntake(1, 1680290288000), // March 31, 2023
                    WaterIntake(1, 1680290288000), // March 31, 2023
                    WaterIntake(1, 1680290288000), // March 31, 2023
                    WaterIntake(1, 1680290288000), // March 31, 2023
                    WaterIntake(1, 1680290288000), // March 31, 2023
                    WaterIntake(1, 1680376688000), // April 1, 2023
                    WaterIntake(1, 1680376688000), // April 1, 2023
                    WaterIntake(1, 1680376688000), // April 1, 2023
                    WaterIntake(1, 1680463088000), // April 2, 2023
                    WaterIntake(1, 1680463088000), // April 2, 2023
                    WaterIntake(1, 1680463088000), // April 2, 2023
                    WaterIntake(1, 1680463088000), // April 2, 2023
                    WaterIntake(1, 1680463088000), // April 2, 2023
                    WaterIntake(1, 1680463088000), // April 2, 2023
                    WaterIntake(1, 1680549488000), // April 3, 2023
                    WaterIntake(1, 1680635888000), // April 4, 2023
                    WaterIntake(1, 1680635888000), // April 4, 2023
                    WaterIntake(1, 1680635888000), // April 4, 2023
                    WaterIntake(1, 1680635888000), // April 4, 2023
                    WaterIntake(1, 1680635888000), // April 4, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680722288000), // April 5, 2023
                    WaterIntake(1, 1680808688000), // April 6, 2023
                    WaterIntake(1, 1680808688000), // April 6, 2023
                    WaterIntake(1, 1680808688000), // April 6, 2023
                    WaterIntake(1, 1680895088000), // April 7, 2023
                    WaterIntake(1, 1680895088000), // April 7, 2023
                    WaterIntake(1, 1680895088000), // April 7, 2023
                    WaterIntake(1, 1680895088000), // April 7, 2023
                    WaterIntake(1, 1680895088000), // April 7, 2023
                    WaterIntake(1, 1680895088000), // April 7, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1680981488000), // April 8, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681000000000), // April 9, 2023
                    WaterIntake(1, 1681154288000), // April 10, 2023
                    WaterIntake(1, 1681240688000), // April 11, 2023
                ),
        )
    }
}
