package com.pandacorp.taskui.presentation.notifications

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.UpdateTaskUseCase
import com.pandacorp.taskui.presentation.ui.activity.MainActivity
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.getParcelableExtraSupport
import com.pandacorp.taskui.presentation.widget.WidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcast : BroadcastReceiver() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var updateTaskUseCase: UpdateTaskUseCase

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = NotificationManagerCompat.from(context)
        val taskItem = intent.getParcelableExtraSupport(Constants.IntentItem, TaskItem::class.java)!!

        when (intent.action) {
            Constants.Notification.ACTION_CREATE -> createNotification(context, taskItem)

            Constants.Notification.ACTION_COMPLETE -> {
                // remove the notification from the notification panel
                notificationManager.cancel(taskItem.id.toInt())
                taskItem.status = TaskItem.COMPLETED
                CoroutineScope(Dispatchers.IO).launch {
                    updateTaskUseCase(taskItem)
                }
                // Send a broadcast to MainTasksFragment to complete the item in the viewModel
                val completeTaskIntent = Intent(Constants.Widget.COMPLETE_TASK_ACTION).apply {
                    putExtra(Constants.IntentItem, taskItem)
                }
                context.sendBroadcast(completeTaskIntent)
                WidgetProvider.update(context)
            }
        }
    }

    private fun createNotification(context: Context, taskItem: TaskItem) {
        // tapResultIntent gets executed when user taps the notification
        val tapResultIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val priority = when (taskItem.priority) {
            null -> NotificationCompat.PRIORITY_MIN
            TaskItem.WHITE -> NotificationCompat.PRIORITY_LOW
            TaskItem.YELLOW -> NotificationCompat.PRIORITY_DEFAULT
            TaskItem.RED -> NotificationCompat.PRIORITY_HIGH
            else -> throw IllegalArgumentException("Unexpected value: ${taskItem.priority}")
        }

        val completeIntent = Intent(context, NotificationBroadcast::class.java).apply {
            putExtra(Constants.IntentItem, taskItem)
            action = Constants.Notification.ACTION_COMPLETE
        }
        val completePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val snoozeIntent = Intent(context, NotificationSnoozeActivity::class.java).apply {
            putExtra(Constants.IntentItem, taskItem)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val snoozePendingIntent = PendingIntent.getActivity(
            context,
            1,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, Constants.Notification.CHANNEL_KEY)
            .setContentTitle(taskItem.text)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setPriority(priority)
            .setContentIntent(tapPendingIntent)
            .addAction(
                0,
                context.resources.getString(R.string.complete),
                completePendingIntent,
            )
            .addAction(
                0,
                context.resources.getString(R.string.snooze),
                snoozePendingIntent,
            )
            .build()

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission, for API > 33
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE,
                )
            }
        } else {
            // Permission has already been granted, perform the operation
            notificationManager.notify(taskItem.id.toInt(), notification)
        }
    }
}