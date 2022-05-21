package com.pandacorp.taskui.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.pandacorp.taskui.MainActivity;
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.ui.main_tasks.PostponeActivity;

import java.util.Random;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager _notificationManager;
    private Context _context;
    private String CHANNEL_ID = String.valueOf(new Random().nextInt(1000));
    private String TIMELINE_CHANNEL_NAME = String.valueOf(new Random().nextInt(1000)) + 1000;

    public static String title;
    public static String content;
    public NotificationUtils(Context base) {
        super(base);
        _context = base;
        createChannel();
    }

    public NotificationCompat.Builder setNotification(String title, String content) {
        this.title = title;
        this.content = content;

        //Code for opening MainActivity after notification click.
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

//        Intent postponeIntent = new Intent(this, PostponeActivity.class);
//        TaskStackBuilder postponeStackBuilder = TaskStackBuilder.create(this);
//        postponeStackBuilder.addNextIntentWithParentStack(postponeIntent);
//        PendingIntent postponePendingIntent =
//                postponeStackBuilder.getPendingIntent(0,
//                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

//        Intent doneIntent = new Intent(this, DoneActivity.class);


        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_complete)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
//                .addAction(R.drawable.ic_notification_postpone, getResources().getString(R.string.postpone), postponePendingIntent)
//                .addAction(R.drawable.ic_complete, getResources().getString(R.string.done), doneIntent)

                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TIMELINE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (_notificationManager == null) {
            _notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return _notificationManager;
    }

    public void setReminder(long timeInMillis) {
        Intent _intent = new Intent(_context, ReminderBroadcast.class);
        PendingIntent _pendingIntent = PendingIntent.getBroadcast(_context, 0, _intent, 0);

        AlarmManager _alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        _alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, _pendingIntent);
    }

}
