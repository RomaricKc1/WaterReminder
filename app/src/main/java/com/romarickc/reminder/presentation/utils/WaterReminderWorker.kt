package com.romarickc.reminder.presentation.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.romarickc.reminder.R
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val NOTIFICATION_ID = 1

class WaterReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val notificationChannelId = "WaterReminder"
    private val notificationChannelName = "Water Reminder"
    private val notificationContentTitle = "Time to drink water!"
    private val notificationContentTextList = listOf(
        "Drink water and feel refreshed!",
        "Stay hydrated and stay healthy!",
        "Water is life. Keep yourself hydrated!",
        "You've got this! Stay hydrated.",
        "Keep calm and drink water.",
        "Drinking water is the key to success.",
        "Your body is thanking you for drinking water.",
        "Stay hydrated and feel the difference.",
        "Good job staying hydrated!",
        "Keep sipping! You're doing great."
    )


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun doWork(): Result {
        // Create a notification to remind the user to drink water
        // TODO: icon not working on buttons
        // Create the "drink" action
        val drinkIntent = Intent(applicationContext, DrinkWaterReceiver::class.java)
        val drinkPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            drinkIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val drinkAction = NotificationCompat.Action(
            R.drawable.baseline_chevron_right_24,
            "Drink",
            drinkPendingIntent
        )

        // Create the "skip" action
        val skipIntent = Intent(applicationContext, SkipWaterReceiver::class.java)
        // skipIntent.action = "com.romarickc.reminder.SKIP_WATER"
        val skipPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            skipIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val skipAction = NotificationCompat.Action(
            R.drawable.baseline_do_not_disturb_24,
            "Skip",
            skipPendingIntent
        )

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

        val notificationContentText =
            notificationContentTextList[(notificationContentTextList.indices).random()]

        // Create the notification
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, notificationChannelId)
                .setContentTitle(notificationContentTitle)
                .setContentText(notificationContentText)
                .setSmallIcon(R.drawable.baseline_water_drop_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .addAction(drinkAction)
                .addAction(skipAction)
                .setAutoCancel(true)

        // create channel
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            notificationChannelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        // Show the notification to the user
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Return failure for now, since we don't have the necessary permission
                Log.i("Worker", "permission not granted")
                return Result.failure()
            }
            try {
                val timeOfDay = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
                return if (timeOfDay in 8..22) {
                    // day time, between 8 AM to 10 PM, only call withing this period
                    notify(NOTIFICATION_ID, notificationBuilder.build())
                    Result.success()
                } else {
                    // night time, between 11 PM to 7 AM -> (timeOfDay <= 7 && timeOfDay >= 23)
                    Result.success()
                }
            } catch (e: SecurityException) {
                // Handle the case where the user has revoked the permission
                return Result.failure()
            }
        }
    }

    // A helper method to schedule periodic work
    companion object {
        fun startPeriodicWork(context: Context) {
            // check if there were something enqueued
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosByTag("water_reminder_periodic_work").get()
            Log.i("worker infos", "$workInfos")

            if (workInfos.isEmpty()) {
                // empty, safe to call
                // consider checking the setting state, maybe the user disabled notifs

                Log.i("notifqueue", "called set periodic")

                // Create a periodic work request to show the notification every x minutes
                val workRequest =
                    PeriodicWorkRequestBuilder<WaterReminderWorker>(1, TimeUnit.HOURS)
                        .setInitialDelay(1, TimeUnit.HOURS)
                        .setConstraints(
                            Constraints.Builder().setRequiresBatteryNotLow(true).build()
                        )
                        .addTag("water_reminder_periodic_work") // set the tag here
                        .build()

                // Enqueue the work request
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "water_reminder_periodic_work",
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    workRequest
                )

            } else {
                // the list is not empty, do not
                Log.i("worker infos", "Already enqueued. Skipping \n$workInfos")
            }
        }
    }
}


class BootReceiverWorker : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            WaterReminderWorker.startPeriodicWork(context!!)
        }
    }
}

@AndroidEntryPoint
class DrinkWaterReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: WaterIntakeRepository

    private fun updatePeriodicWorkInterval(application: Context, interval: Long) {
        Log.i("drink notif", "called")
        var workInfos =
            WorkManager.getInstance(application)
                .getWorkInfosByTag("water_reminder_periodic_work").get()

        Log.i("worker settings", "current cancelling and recreating $workInfos")
        val workRequest =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(interval, TimeUnit.HOURS)
                .setInitialDelay(interval, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .addTag("water_reminder_periodic_work") // set the tag here
                .build()

        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            "water_reminder_periodic_work",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
        workInfos =
            WorkManager.getInstance(application)
                .getWorkInfosByTag("water_reminder_periodic_work").get()

        Log.i("worker register intake", "new one -> $workInfos")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the "drink" action here
        Log.i("Notif receiver", "acked")

        GlobalScope.launch {
            Log.d("insertnotif", "inserted")
            repository.insertIntake()

            // change the next notification date
            // update the worker to not send any notification that is like a minute away
            // Toast.makeText(application, "$notifPref", Toast.LENGTH_SHORT).show()
            when (repository.getNotifPref(1).firstOrNull() ?: 0) {
                1 -> {
                    // the 3 hours interval
                    updatePeriodicWorkInterval(context, 3)
                }
                2 -> {
                    // notification disabled, so I don't care
                }
                0 -> {
                    // the 1 hour interval
                    updatePeriodicWorkInterval(context, 1)
                }
            }
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

class SkipWaterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the "skip" action here
        Log.i("Notif receiver", "skipped $context")

        /*context.startActivity(Intent(context.applicationContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        Toast.makeText(context, "Skipped drinking water", Toast.LENGTH_SHORT).show()
        */
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}