package com.pandacorp.taskui.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Setting of notification id needed for deleting it in future
        SharedPreferences sp = context.getSharedPreferences("notifications_id", Context.MODE_PRIVATE);
        int id = sp.getInt(NotificationUtils.content, 0);

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        NotificationUtils _notificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder _builder = _notificationUtils.setNotification(title, content);
        _notificationUtils.getManager().notify(id, _builder.build());
    }

}
