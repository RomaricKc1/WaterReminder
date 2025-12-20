package com.romarickc.reminder.commons

import androidx.core.app.NotificationCompat
import com.romarickc.reminder.domain.model.WaterIntake
import java.util.concurrent.TimeUnit

data class NotifActions(
    val drinkAction: NotificationCompat.Action,
    val skipAction: NotificationCompat.Action,
    val openAppAction: NotificationCompat.Action,
)

data class NotifConf(
    val notifId: Int,
    val flag: Int,
    val timeRangeAfter: Int,
    val timeRangeBefore: Int,
)

data class NotifElms(
    val channelId: String,
    val channelName: String,
    val title: String,
    val text: String,
    val priority: Int,
    val autoCancel: Boolean,
)

data class WorkerConf(
    val interval: Long,
    val timeUnit: TimeUnit,
    val onlyOnNotLowBattery: Boolean,
)

@Suppress("ktlint:standard:class-naming")
enum class E_Languages(
    val value: Int,
) {
    FRANCAIS(0),
    ENGLISH(1),
    ;

    companion object {
        fun fromValue(value: Int): E_Languages = entries.find { it.value == value } ?: FRANCAIS

        fun toValue(lang: E_Languages): Int = lang.value
    }
}

val listIntakes =
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
    )
