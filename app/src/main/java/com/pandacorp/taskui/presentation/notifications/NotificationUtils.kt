package com.pandacorp.taskui.presentation.notifications
//
// import android.app.*
// import android.content.Context
// import android.content.ContextWrapper
// import android.content.Intent
// import android.os.Build
// import androidx.core.app.NotificationCompat
// import com.pandacorp.taskui.R
// import com.pandacorp.taskui.presentation.ui.MainActivity
// import java.util.*
//
// class NotificationUtils(private val context: Context) : ContextWrapper(
//         context) {
//     private var _notificationManager: NotificationManager? = null
//     private val CHANNEL_ID = Random().nextInt(1000).toString() //TODO: What is this?
//     private val TIMELINE_CHANNEL_NAME = Random().nextInt(1000).toString() + 1000
//
//     fun setNotification(title: String?, content: String, timeInMillis: Long): NotificationCompat.Builder {
//         //Code for opening MainActivity after notification click.
//         val intent = Intent(context, ReminderBroadcast::class.java)
//         intent.putExtra("title", title)
//         intent.putExtra("content", Companion.content)
//         pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//         alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//         alarmManager!![AlarmManager.RTC_WAKEUP, timeInMillis] =
//             pendingIntent
//
//         val resultIntent = Intent(this, MainActivity::class.java)
//         val stackBuilder = TaskStackBuilder.create(this)
//         stackBuilder.addNextIntentWithParentStack(resultIntent)
//         val resultPendingIntent = stackBuilder.getPendingIntent(
//                 0,
//                 PendingIntent.FLAG_UPDATE_CURRENT)
//         return NotificationCompat.Builder(this, CHANNEL_ID)
//             .setSmallIcon(R.drawable.ic_complete)
//             .setContentTitle(title)
//             .setContentText(content)
//             .setAutoCancel(true)
//             .setContentIntent(resultPendingIntent)
//             .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//     }
//
//     private fun createChannel() {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             val channel = NotificationChannel(
//                     CHANNEL_ID,
//                     TIMELINE_CHANNEL_NAME,
//                     NotificationManager.IMPORTANCE_DEFAULT)
//             channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//             manager!!.createNotificationChannel(channel)
//         }
//     }
//
//     val manager: NotificationManager?
//         get() {
//             if (_notificationManager == null) {
//                 _notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//             }
//             return _notificationManager
//         }
//
//     companion object {
//         var content: String? = null
//         private var pendingIntent: PendingIntent? = null
//         private var alarmManager: AlarmManager? = null
//         fun cancelNotification(context: Context, notifyId: Int) {
//             if (alarmManager != null) {
//                 alarmManager!!.cancel(pendingIntent)
//                 val notificationManager =
//                     context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                 notificationManager.cancel(notifyId)
//             }
//         }
//     }
//
//     init {
//         createChannel()
//     }
// }