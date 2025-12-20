package com.romarickc.reminder.commons

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

object Constants {
    // core
    const val DB_NOTIF_LEVEL_IDX: Int = 1
    const val SHARED_DATA = "app_pref"
    const val IMPORT_FILE_PREFIX = "com.romarickc.reminder_"
    const val WORKER_TAG_NAME = "water_reminder_periodic_work"

    const val LANG_KEY = "lang"
    const val DEF_LANG = "ENGLISH"
    const val LANG_EN = "ENGLISH"
    const val LANG_FR = "FRANÇAIS"

    // intakes
    const val MAX_INTAKE: Int = 99
    const val MIN_INTAKE: Int = 8
    const val RECOMMENDED_INTAKE: Int = 12
    const val STANDARD_GLASS_L = 0.25
    const val REFRESH_INTERVAL_TILE_MS: Long = 1 * 60 * 1000L

    // time stuff
    const val MONTHS_CNT = 12
    const val SEC_MS = 1000
    const val MIN_SEC = 60
    const val HOUR_SEC = 3600
    const val DAY_SEC = 86400
    const val YEAR_SEC = 31536000

    // notifs
    const val THREE_HOURS_INTERVAL = 3
    const val ONE_HOUR_INTERVAL = 1
    const val NOTIF_THREE_HOURS_MODE = 1
    const val NOTIF_ONE_HOURS_MODE = 3
    const val NOTIF_DISABLED_MODE = 2
    const val TIME_8_AM_INCLUS = 8
    const val TIME_22_H_INCLUS = 22

    // permission
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val PERMISSION_TO_RQ = Manifest.permission.POST_NOTIFICATIONS
}
