package com.romarickc.reminder.commons

import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.gson.annotations.SerializedName
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
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

data class AnyState<out T>(
    val data: T? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

data class ExportIntakesStream(
    @SerializedName("line") val line: String,
)

data class ServerPing(
    @SerializedName("response") val response: String,
)

data class ExportIntakesRequest(
    @SerializedName("line") val line: String,
)

data class ExportIntakesResponse(
    @SerializedName("res") val res: String,
)

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(
        data: T?,
        message: String,
    ) : Resource<T>(data, message)

    class Error<T>(
        message: String,
        data: T? = null,
    ) : Resource<T>(data, message)
}

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

@Suppress("ktlint:standard:class-naming")
enum class E_ImportServerError(
    val value: Int,
) {
    SUCCESS(0),
    CONV_STR_DATA_ERROR(1),
    OTHER_ERROR(2),
    INIT(3),
}

@Suppress("ktlint:standard:class-naming")
enum class E_NotifPeriod(
    val value: Int,
) {
    ONE_HOUR_MODE(0),
    THREE_HOURS_MODE(1),
    DISABLED_MODE(2),
    ;

    companion object {
        fun fromValue(value: Int): E_NotifPeriod = entries.find { it.value == value } ?: E_NotifPeriod.ONE_HOUR_MODE

        fun toValue(lang: E_NotifPeriod): Int = lang.value
    }
}

object QrGenerator {
    fun encodeAsBitmap(
        str: String,
        width: Int,
        height: Int,
    ): Bitmap? =
        try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, height)
            val bmp = createBitmap(width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] =
                        if (bitMatrix.get(
                                x,
                                y,
                            )
                        ) {
                            android.graphics.Color.BLACK
                        } else {
                            android.graphics.Color.WHITE
                        }
                }
            }
            bmp
        } catch (e: Exception) {
            null
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
