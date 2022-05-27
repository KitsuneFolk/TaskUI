package com.pandacorp.taskui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Getting notification title and content

        NotificationUtils _notificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder _builder = _notificationUtils.setNotification(NotificationUtils.title, NotificationUtils.content);
        _notificationUtils.getManager().notify(101, _builder.build());
    }
}
