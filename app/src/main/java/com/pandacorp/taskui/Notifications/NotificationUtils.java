package com.pandacorp.taskui.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.pandacorp.taskui.MainActivity;
import com.pandacorp.taskui.R;

import java.util.Random;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager _notificationManager;
    private Context context;
    private String CHANNEL_ID = String.valueOf(new Random().nextInt(1000));
    private String TIMELINE_CHANNEL_NAME = String.valueOf(new Random().nextInt(1000)) + 1000;

    private static long timeInMillis;

    public String title;
    public static String content;
    public NotificationUtils(Context context) {
        super(context);
        this.context = context;
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


        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_complete)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)

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
    private static PendingIntent pendingIntent;
    private static AlarmManager alarmManager;
    public void setReminder(long timeInMillis) {
        this.timeInMillis = timeInMillis;
        Intent intent = new Intent(context, ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);


    }
    public static void cancelNotification(Context context, int notifyId){
        alarmManager.cancel(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyId);


    }


}
