package com.romarickc.reminder.commons

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.tiles.RequestBuilders
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.luben.zstd.Zstd
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants.DAY_SEC
import com.romarickc.reminder.commons.Constants.DB_NOTIF_LEVEL_IDX
import com.romarickc.reminder.commons.Constants.DEF_LANG
import com.romarickc.reminder.commons.Constants.HOUR_SEC
import com.romarickc.reminder.commons.Constants.IMPORT_FILE_PREFIX
import com.romarickc.reminder.commons.Constants.LANG_EN
import com.romarickc.reminder.commons.Constants.LANG_FR
import com.romarickc.reminder.commons.Constants.LANG_KEY
import com.romarickc.reminder.commons.Constants.MIN_SEC
import com.romarickc.reminder.commons.Constants.ONE_HOUR_INTERVAL
import com.romarickc.reminder.commons.Constants.SERVER_ADDR
import com.romarickc.reminder.commons.Constants.SERVER_ADDR_KEY
import com.romarickc.reminder.commons.Constants.SHARED_DATA
import com.romarickc.reminder.commons.Constants.THREE_HOURS_INTERVAL
import com.romarickc.reminder.commons.Constants.WORKER_TAG_NAME
import com.romarickc.reminder.commons.Constants.YEAR_SEC
import com.romarickc.reminder.domain.model.WaterIntake
import com.romarickc.reminder.domain.repository.CommRepository
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun storeSharedPref(
    base: String,
    key: String,
    value: String,
    context: Context,
) {
    val sharedPreferences =
        context
            .getSharedPreferences(base, Context.MODE_PRIVATE)
    sharedPreferences.edit { putString(key, value) }
}

fun loadSharedPref(
    base: String,
    key: String,
    def: String,
    context: Context,
): String {
    val sharedPreferences =
        context
            .getSharedPreferences(base, Context.MODE_PRIVATE)
    val ret = sharedPreferences.getString(key, def)!!
    return ret
}

fun loadLanguage(context: Context) {
    val lang =
        loadSharedPref(
            SHARED_DATA,
            LANG_KEY,
            DEF_LANG,
            context,
        )
    var locale: Locale = Locale.FRENCH
    when (lang) {
        LANG_FR -> {
            locale = Locale.FRENCH
        }

        LANG_EN -> {
            locale = Locale.ENGLISH
        }

        else -> {}
    }
    println("Loading language -> $locale")

    val appLocale = LocaleListCompat.create(locale)
    AppCompatDelegate.setApplicationLocales(appLocale)

    val configuration = Configuration()
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)
    // context.createConfigurationContext(configuration)
    context.resources.updateConfiguration(
        configuration,
        context.resources.displayMetrics,
    )
}

// /////////////////////////////////////////////////////////////////////////////////////////////////
// intake graphs
fun mapHeight(
    value: Float,
    minValue: Float,
    maxValue: Float,
    minDrawHeight: Float,
    maxDrawHeight: Float,
): Float {
    var ret =
        ((value - minValue) / (maxValue - minValue)) * (maxDrawHeight - minDrawHeight)
    ret += minDrawHeight
    return ret
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
    // Log.i("data avg", "current month: $currentMonth")
    var totalIntake = 0
    for (month in 1..currentMonth) {
        totalIntake += intakeData.getOrDefault(month, 0)
    }
    return totalIntake.toFloat() / currentMonth
}

@Composable
fun getTimeAgo(timestamp: Long): String {
    // TODO, implement leap year version also
    val currentTime = Instant.now().toEpochMilli()
    val timeDiff = (currentTime - timestamp) / 1000

    // timeDiff in s
    return when {
        timeDiff < MIN_SEC -> {
            stringResource(R.string.just_now)
        }

        timeDiff < HOUR_SEC -> {
            if (timeDiff / MIN_SEC > 1) {
                stringResource(R.string.minutes_ago).format(timeDiff / MIN_SEC)
            } else {
                stringResource(R.string.minute_ago).format(timeDiff / MIN_SEC)
            }
        }

        // less than an hour (59m)
        timeDiff < DAY_SEC -> {
            if (timeDiff / HOUR_SEC > 1) {
                stringResource(R.string.hours_ago).format(timeDiff / HOUR_SEC)
            } else {
                stringResource(R.string.hour_ago).format(timeDiff / HOUR_SEC)
            }
        }

        // 23h
        timeDiff < YEAR_SEC -> {
            if (timeDiff / DAY_SEC > 1) {
                stringResource(R.string.days_ago).format(timeDiff / DAY_SEC)
            } else {
                stringResource(R.string.day_ago).format(timeDiff / DAY_SEC)
            }
        }

        // 365
        else -> {
            if (timeDiff / YEAR_SEC > 1) {
                stringResource(R.string.years_ago).format(timeDiff / YEAR_SEC)
            } else {
                stringResource(R.string.year_ago).format(timeDiff / YEAR_SEC)
            }
        } // xyo
    }
}

fun getTimeTxt(
    timestamp: Long,
    explicitUTC: Any? = null,
): String {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    if (explicitUTC != null) {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }
    return dateFormat.format(date)
}

// /////////////////////////////////////////////////////////////////////////////////////////////////
// periodic worker
suspend fun reSchedPeriodicWork(
    context: Context,
    notifPref: E_NotifPeriod,
    careAboutDisabled: Boolean,
) {
    when (notifPref) {
        E_NotifPeriod.ONE_HOUR_MODE -> {
            Log.i("settings", "1h mode notif")
            updatePeriodicWorkInterval(
                context = context,
                interval = ONE_HOUR_INTERVAL.toLong(),
            )
        }

        E_NotifPeriod.THREE_HOURS_MODE -> {
            Log.i("settings", "3h mode notif")
            updatePeriodicWorkInterval(
                context = context,
                interval = THREE_HOURS_INTERVAL.toLong(),
            )
        }

        E_NotifPeriod.DISABLED_MODE -> {
            if (careAboutDisabled) {
                // cancel/disable
                WorkManager
                    .getInstance(context)
                    .cancelAllWorkByTag(WORKER_TAG_NAME)
                Log.i("settings", "Cancelled notif")

                val workInfos =
                    withContext(Dispatchers.IO) {
                        WorkManager
                            .getInstance(context)
                            .getWorkInfosByTag(WORKER_TAG_NAME)
                            .get()
                    }
                Log.i("new worker infos settings", "$workInfos")
            }
            // else notification disabled, so I don't care
        }
    }
}

fun updatePeriodicWorkInterval(
    context: Context,
    interval: Long,
) {
    var workInfos =
        WorkManager
            .getInstance(context)
            .getWorkInfosByTag(WORKER_TAG_NAME)
            .get()

    val dtNow =
        Instant
            .ofEpochSecond(System.currentTimeMillis() / 1000L)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

    var dtNext =
        Instant
            .ofEpochSecond(workInfos[0].nextScheduleTimeMillis / 1000L)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

    Log.i("worker settings", "current time $dtNow")
    Log.i("worker settings", "current worker $workInfos")
    Log.i("worker settings", "current next-sched $dtNext, requested interval -> $interval")

    val workRequest =
        PeriodicWorkRequestBuilder<WaterReminderWorker>(
            interval,
            TimeUnit.HOURS,
        ).setInitialDelay(interval, TimeUnit.HOURS)
            .setConstraints(
                Constraints
                    .Builder()
                    .setRequiresBatteryNotLow(true)
                    .build(),
            ).addTag(WORKER_TAG_NAME) // set the tag here
            .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORKER_TAG_NAME,
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        workRequest,
    )
    workInfos =
        WorkManager
            .getInstance(context)
            .getWorkInfosByTag(WORKER_TAG_NAME)
            .get()

    dtNext =
        Instant
            .ofEpochSecond(workInfos[0].nextScheduleTimeMillis / 1000L)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    Log.i("worker settings", "cancelled and recreated new one -> $workInfos")
    Log.i("worker settings", "next-sched $dtNext")
}

fun startPeriodicWork(
    context: Context,
    workerConf: WorkerConf,
) {
    val workInfos =
        WorkManager
            .getInstance(context)
            .getWorkInfosByTag(WORKER_TAG_NAME)
            .get()
    Log.i("worker infos", "$workInfos")

    if (workInfos.isEmpty()) {
        // empty, safe to call
        // consider checking the setting state, maybe the user disabled notifs
        Log.i("notifqueue", "called set periodic")

        // Create a periodic work request to show the notification every x time unit
        val workRequest =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(
                workerConf.interval,
                workerConf.timeUnit,
            ).setInitialDelay(workerConf.interval, workerConf.timeUnit)
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiresBatteryNotLow(workerConf.onlyOnNotLowBattery)
                        .build(),
                ).addTag(WORKER_TAG_NAME) // set the tag here
                .build()

        // Enqueue the work request
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORKER_TAG_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest,
        )
    } else {
        // the list is not empty, do not
        Log.i("worker infos", "Already enqueued. Skipping \n$workInfos")
    }
}

// /////////////////////////////////////////////////////////////////////////////////////////////////
// import export
@Deprecated("Using file system. I don't like it anymore. Use http server comm")
fun getFileToImport(thePath: String): File {
    val availableForImport = mutableListOf<File>()

    File(thePath).walk().forEach {
        if (it.path.contains(IMPORT_FILE_PREFIX)) {
            availableForImport += it
        }
        Log.i("files found", "$it, ${it.path}")
    }
    // Log.i("files avail", "$availableForImport")
    // get the latest exported data
    var pickedFile = availableForImport[0]
    for (pathfound in availableForImport) {
        val pathTimestamp =
            pathfound.path
                .split("_")[1]
                .split(".")[0]
        val pickedFileTimestamp =
            pickedFile.path
                .split("_")[1]
                .split(".")[0]

        if (pickedFileTimestamp.toLong() < pathTimestamp.toLong()) {
            pickedFile = pathfound
        }
        Log.i(
            "files found",
            "${pathfound.path} --> $pathTimestamp",
        )
    }
    return pickedFile
}

@Deprecated("Using file system. I don't like it anymore. Use http server comm")
fun getEntriesFromFile(thePath: String): List<WaterIntake>? {
    try {
        // val documentsDirectory =
        //    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(thePath)
        val lines = file.readLines()

        val data =
            lines.map { line ->
                // file format is "id,timestamp"
                val (_, timestamp) = line.split(",")
                WaterIntake(null, timestamp.toLongOrNull())
            }

        return data
    } catch (e: Exception) {
        Log.e("import-e", "$e")
        // e.printStackTrace()
        return null
    }
}

@Deprecated("Using file system. I don't like it anymore. Use http server comm")
suspend fun exportToFile(
    thePath: String,
    data: List<WaterIntake>,
): Int {
    /*val documentsDirectory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val file = File(documentsDirectory, filePath)*/
    val file = File(thePath)
    try {
        val fileOutputStream =
            withContext(Dispatchers.IO) {
                FileOutputStream(file)
            }
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        val bufferedWriter = BufferedWriter(outputStreamWriter)

        for (item in data) {
            withContext(Dispatchers.IO) {
                bufferedWriter.write("${item.id},${item.timestamp}")
            }
            withContext(Dispatchers.IO) {
                bufferedWriter.newLine()
            }
        }

        withContext(Dispatchers.IO) {
            bufferedWriter.flush()
        }
        withContext(Dispatchers.IO) {
            bufferedWriter.close()
        }
        Log.i("export", "done")
        return 0
    } catch (e: IOException) {
        // e.printStackTrace()
        Log.i("export-e", "$e")
        return -1
    }
}

suspend fun exportToSever(
    application: Application,
    repository: WaterIntakeRepository,
    commRepository: CommRepository,
): ByteArray? {
    val stream = repository.getAllToStr()
    val duration = Toast.LENGTH_SHORT

    if (stream == null) {
        val toast =
            Toast.makeText(
                application,
                application
                    .getString(R.string.empty_export),
                duration,
            )

        toast.show()
        return null
    }

    val compressed = compressStringZstd(stream)
    Log.i(
        "compression",
        "from ${stream.toByteArray(Charsets.UTF_8).size} bytes to ${compressed.size}",
    )

    val req = ExportIntakesRequest(stream)
    val resp = commRepository.putIntakesServer(req)

    if (resp.data == null) {
        Log.i("export server", "data is null. server response -> ${resp.message}")
        val toast =
            Toast.makeText(
                application,
                application
                    .getString(R.string.export_failure),
                duration,
            )

        toast.show()
        return null
    }

    resp.data.let { response ->
        Log.i(
            "import_export",
            "exported to the server. ret -> $response",
        )
        val toast =
            Toast.makeText(
                application,
                application
                    .getString(R.string.export_success),
                duration,
            )
        toast.show()
    }
    return compressed
}

fun importHelper(
    application: Application,
    importStatus: E_ImportServerError,
) {
    var msg = ""
    val duration = Toast.LENGTH_SHORT

    when (importStatus) {
        E_ImportServerError.INIT -> {
        }

        E_ImportServerError.SUCCESS -> {
            msg = application.getString(R.string.import_success)
        }

        E_ImportServerError.CONV_STR_DATA_ERROR -> {
            msg = application.getString(R.string.str_to_intakes_error)
        }

        E_ImportServerError.OTHER_ERROR -> {
            msg = application.getString(R.string.import_failure)
        }
    }
    val toast = Toast.makeText(application, msg, duration)
    toast.show()
}

fun compressStringZstd(s: String): ByteArray {
    val input = s.toByteArray(Charsets.UTF_8)
    return Zstd.compress(input, Constants.ZSTD_COMPRESSION_LEVEL)
}

// /////////////////////////////////////////////////////////////////////////////////////////////////
// notification
fun showNotification(
    context: Context,
    notifConf: NotifConf,
    notificationBuilder: NotificationCompat.Builder,
): ListenableWorker.Result {
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // failure for now, since we don't have the necessary permission
            Log.i("Worker", "permission not granted")
            return ListenableWorker.Result.failure()
        }
        try {
            val timeOfDay = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
            return if (
                timeOfDay in notifConf.timeRangeAfter..notifConf.timeRangeBefore
            ) {
                // day time, between rangeAfter to rangeBefore, only call withing this period
                notify(notifConf.notifId, notificationBuilder.build())
                ListenableWorker.Result.success()
            } else {
                ListenableWorker.Result.success()
            }
        } catch (_: SecurityException) {
            return ListenableWorker.Result.failure()
        }
    }
}

fun getNotifActions(
    context: Context,
    notifConf: NotifConf,
): NotifActions {
    // TODO: icon not working on buttons
    // Create the "drink" action
    val drinkIntent =
        Intent(context, DrinkWaterReceiver::class.java)
    val drinkPendingIntent =
        PendingIntent.getBroadcast(
            context,
            notifConf.notifId,
            drinkIntent,
            notifConf.flag,
        )
    val drinkAction =
        NotificationCompat.Action(
            R.drawable.baseline_chevron_right_24,
            context.getString(R.string.drink),
            drinkPendingIntent,
        )

    // Create the "skip" action
    val skipIntent =
        Intent(context, SkipWaterReceiver::class.java)
    val skipPendingIntent =
        PendingIntent.getBroadcast(
            context,
            notifConf.notifId,
            skipIntent,
            notifConf.flag,
        )
    val skipAction =
        NotificationCompat.Action(
            R.drawable.baseline_do_not_disturb_24,
            context.getString(R.string.skip),
            skipPendingIntent,
        )

    // Create the "open app" action
    val openAppIntent =
        Intent(context, MainActivity::class.java)
    val openAppPendingIntent =
        PendingIntent.getActivity(
            context,
            notifConf.notifId,
            openAppIntent,
            notifConf.flag,
        )

    val openAppAction =
        NotificationCompat.Action(
            R.drawable.baseline_chevron_right_24,
            context.getString(R.string.open_app),
            openAppPendingIntent,
        )

    return NotifActions(
        drinkAction,
        skipAction,
        openAppAction,
    )
}

fun notifChannel(
    notifElms: NotifElms,
    importance: Int,
): NotificationChannel =
    NotificationChannel(
        notifElms.channelId,
        notifElms.channelName,
        importance,
    )

fun notifBuilder(
    context: Context,
    notifConf: NotifConf,
    notifElms: NotifElms,
): NotificationCompat.Builder {
    val notifActions =
        getNotifActions(context, notifConf)
    // Create the notification
    return NotificationCompat
        .Builder(context, notifElms.channelId)
        .setContentTitle(notifElms.title)
        .setContentText(notifElms.text)
        .setSmallIcon(R.drawable.baseline_water_drop_24)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setPriority(notifElms.priority)
        .addAction(notifActions.drinkAction)
        .addAction(notifActions.skipAction)
        .addAction(notifActions.openAppAction)
        .setAutoCancel(notifElms.autoCancel)
}

fun getRandNotifMsg(context: Context): String {
    val notificationContentTextList =
        listOf(
            context.getString(R.string.notif_text1),
            context.getString(R.string.notif_text2),
            context.getString(R.string.notif_text3),
            context.getString(R.string.notif_text4),
            context.getString(R.string.notif_text5),
            context.getString(R.string.notif_text6),
            context.getString(R.string.notif_text7),
            context.getString(R.string.notif_text8),
            context.getString(R.string.notif_text9),
            context.getString(R.string.notif_text10),
        )

    return notificationContentTextList[(notificationContentTextList.indices).random()]
}

// /////////////////////////////////////////////////////////////////////////////////////////////////
// tiles
fun getTimeLineBuilder(layoutElement: LayoutElement): Timeline = Timeline.fromLayoutElement(layoutElement)

fun openAppMod(packageName: String): ActionBuilders.LaunchAction =
    ActionBuilders.LaunchAction
        .Builder()
        .setAndroidActivity(
            ActionBuilders.AndroidActivity
                .Builder()
                .setClassName(
                    MainActivity::class.qualifiedName ?: "",
                ).setPackageName(packageName)
                .build(),
        ).build()

suspend fun checkClickIdAction(
    application: Context,
    requestParams: RequestBuilders.TileRequest,
    repository: WaterIntakeRepository,
) {
    when (requestParams.currentState.lastClickableId) {
        "ID_CLICK_RM_INTAKE" -> {
            Log.i("tile action", "rm intake")
            repository.removeLastIntake()

            Toast
                .makeText(
                    application,
                    application.getString(R.string.removed_last),
                    Toast.LENGTH_SHORT,
                ).show()
        }

        "ID_CLICK_ADD_INTAKE" -> {
            Log.i("tile action", "add intake")
            repository.insertIntake()

            // update the worker to not send any notification that is like a minute away
            val notifPref =
                E_NotifPeriod.fromValue(
                    repository
                        .getNotifPref(
                            DB_NOTIF_LEVEL_IDX,
                        ).firstOrNull() ?: E_NotifPeriod.ONE_HOUR_MODE.value,
                )
            Toast
                .makeText(
                    application,
                    application.getString(R.string.registered),
                    Toast.LENGTH_SHORT,
                ).show()

            reSchedPeriodicWork(
                context = application,
                notifPref = notifPref,
                careAboutDisabled = false,
            )
        }

        else -> {
        }
    }
}

suspend fun getCurrentIntakeTile(repository: WaterIntakeRepository): Int {
    val now: ZonedDateTime = ZonedDateTime.now()
    val startOfDay: ZonedDateTime = now.toLocalDate().atStartOfDay(now.zone)
    val startOfDayTimestamp = startOfDay.toInstant().toEpochMilli()

    val currentIntake: Int =
        withContext(Dispatchers.IO) {
            repository.getCountTgtThis(startOfDayTimestamp).first()
        }

    return currentIntake
}

suspend fun getTargetTile(repository: WaterIntakeRepository): Int {
    val targetVal: Int =
        withContext(Dispatchers.IO) {
            repository.getTarget(1).first()
        }

    return targetVal
}

fun <T> readTypeCompanion(
    anyState: AnyState<T>,
    res: Resource<T>,
): AnyState<T> =
    when (res) {
        is Resource.Success -> {
            anyState
                .copy(
                    data = res.data,
                    loading = false,
                    error = null,
                ).also {
                    Log.i("anystate", "success.")
                }
        }

        is Resource.Error -> {
            anyState
                .copy(data = null, loading = false, error = res.message)
                .also {
                    Log.i("anystate", "failure.")
                }
        }
    }

// import export: comm with server
fun loadServerAddress(context: Context): String {
    val address =
        loadSharedPref(
            SHARED_DATA,
            SERVER_ADDR_KEY,
            SERVER_ADDR,
            context,
        )

    println("Loading server address -> $address")
    return address
}

fun updateServerAddress(
    target: String,
    context: Context,
) {
    storeSharedPref(
        SHARED_DATA,
        SERVER_ADDR_KEY,
        target,
        context,
    )
    Toast
        .makeText(
            context,
            "You'll need to restart the app by yourself now",
            Toast.LENGTH_LONG,
        ).show()
}
