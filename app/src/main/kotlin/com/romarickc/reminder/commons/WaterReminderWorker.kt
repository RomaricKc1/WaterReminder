package com.romarickc.reminder.commons

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants.DB_NOTIF_LEVEL_IDX
import com.romarickc.reminder.commons.Constants.ONE_HOUR_INTERVAL
import com.romarickc.reminder.commons.Constants.TIME_22_H_INCLUS
import com.romarickc.reminder.commons.Constants.TIME_8_AM_INCLUS
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val NOTIFICATION_ID = 1

class WaterReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {
    private val notificationChannelId = "WaterReminder"

    // Create a notification to remind the user to drink water
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun doWork(): Result {
        loadLanguage(applicationContext)

        val notificationChannelName =
            applicationContext
                .getString(R.string.notif_channel_name)
        val notificationContentTitle =
            applicationContext
                .getString(R.string.notif_title)

        val notificationContentText = getRandNotifMsg(applicationContext)

        val notifElms =
            NotifElms(
                channelId = notificationChannelId,
                channelName = notificationChannelName,
                title = notificationContentTitle,
                text = notificationContentText,
                priority = NotificationCompat.PRIORITY_HIGH,
                autoCancel = true,
            )
        val notifConf =
            NotifConf(
                notifId = NOTIFICATION_ID,
                flag = PendingIntent.FLAG_IMMUTABLE,
                timeRangeAfter = TIME_8_AM_INCLUS,
                timeRangeBefore = TIME_22_H_INCLUS,
            )

        // Create channel
        val notificationManager =
            applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            notifChannel(
                notifElms = notifElms,
                importance = NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )

        // Show the notification to the user at day time, between 8 AM to 10 PM,
        // only call withing this period
        return showNotification(
            applicationContext,
            notifConf,
            notificationBuilder =
                notifBuilder(
                    applicationContext,
                    notifConf = notifConf,
                    notifElms = notifElms,
                ),
        )
    }

    // A helper method to schedule periodic work
    companion object {
        fun initWorker(
            context: Context,
            workerConf: WorkerConf,
        ) {
            startPeriodicWork(context, workerConf)
        }
    }
}

class BootReceiverWorker : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        val workerConf =
            WorkerConf(
                timeUnit = TimeUnit.HOURS,
                onlyOnNotLowBattery = true,
                interval = ONE_HOUR_INTERVAL.toLong(),
            )
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            WaterReminderWorker.initWorker(context!!, workerConf)
        }
    }
}

@AndroidEntryPoint
class DrinkWaterReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        // Handle the "drink" action here
        Log.i("Notif receiver", "acked")

        GlobalScope.launch {
            repository.insertIntake()
            // change the next notification date
            // update the worker to not send any notification that is like a minute away
            val notifPref =
                E_NotifPeriod.fromValue(
                    repository
                        .getNotifPref(
                            DB_NOTIF_LEVEL_IDX,
                        ).firstOrNull() ?: E_NotifPeriod.ONE_HOUR_MODE.value,
                )
            reSchedPeriodicWork(
                context = context,
                notifPref = notifPref,
                careAboutDisabled = false,
            )
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

class SkipWaterReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        // Handle the "skip" action here
        Log.i("Notif receiver", "skipped $context")

        /* // Can't toast on a thread that has not called Looper.prepare()
        Toast
            .makeText(
                context,
                context.getString(R.string.skipped),
                Toast.LENGTH_SHORT,
            ).show()*/
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
