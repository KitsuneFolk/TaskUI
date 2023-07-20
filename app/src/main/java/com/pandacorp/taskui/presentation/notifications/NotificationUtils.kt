package com.pandacorp.taskui.presentation.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.activity.MainActivity
import com.pandacorp.taskui.presentation.utils.Constants

object NotificationUtils {
    fun cancel(context: Context, taskItem: TaskItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, taskItem.id.toInt(), intent, PendingIntent
                .FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun create(context: Context, taskItem: TaskItem, snoozedTime: Long = -1L) {
        createChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val createIntent = Intent(context, NotificationBroadcast::class.java).apply {
            putExtra(Constants.IntentItem, taskItem)
            action = Constants.Notification.ACTION_CREATE
        }
        val notificationPendingIntent =
            PendingIntent.getBroadcast(context, taskItem.id.toInt(), createIntent, PendingIntent.FLAG_IMMUTABLE)
        val mainActivityPendingIntent = PendingIntent.getActivity(
            context, taskItem.id.toInt(), Intent(context, MainActivity::class.java), PendingIntent
                .FLAG_IMMUTABLE
        )
        val clockInfo =
            if (snoozedTime == -1L) AlarmManager.AlarmClockInfo(taskItem.time!!, mainActivityPendingIntent)
            else AlarmManager.AlarmClockInfo(snoozedTime, mainActivityPendingIntent)

        alarmManager.setAlarmClock(clockInfo, notificationPendingIntent)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                Constants.Notification.CHANNEL_KEY, "Tasks Notification Channel",
                importance
            ).apply {
                description = "Notification for Tasks"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun snoozeNotification(context: Context, taskItem: TaskItem, snoozedTime: Long) {
        NotificationManagerCompat.from(context)
            .cancel(taskItem.id.toInt()) // remove the notification from the notification panel
        cancel(context, taskItem)
        create(context, taskItem, snoozedTime)
    }

    fun cancelAll(context: Context, taskItemList: List<TaskItem>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        taskItemList.forEach {
            val intent = Intent(context, NotificationBroadcast::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, it.id.toInt(), intent, PendingIntent
                    .FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}