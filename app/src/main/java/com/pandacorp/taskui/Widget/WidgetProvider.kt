package com.pandacorp.taskui.Widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.DBHelper
import com.pandacorp.taskui.MainActivity
import com.pandacorp.taskui.R
import com.pandacorp.taskui.SetTaskActivity
import com.pandacorp.taskui.ui.settings.MySettings


class WidgetProvider : AppWidgetProvider() {
    private val TAG = "MyLogs"
    
    private val COMPLETE_BUTTON_ACTION = "widget_complete_btn"
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(TAG, "onUpdate: onUpdate")
        updateWidget(context, appWidgetManager, appWidgetIds)
        
    }
    
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val widget_views = RemoteViews(
                    context.packageName,
                    R.layout.widget
            )
            
            //Code needed for handling the complete button in widget list item.
            val intent = Intent(context, WidgetService::class.java)
            widget_views.setRemoteAdapter(R.id.widget_listView, intent)
            widget_views.setOnClickPendingIntent(
                    R.id.widget_complete_button,
                    getPendingSelfIntent(context, COMPLETE_BUTTON_ACTION))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            widget_views.setRemoteAdapter(appWidgetId, R.id.widget_listView, intent)
            val completeTaskIntent = Intent(context, WidgetProvider::class.java)
            completeTaskIntent.action = COMPLETE_TASK_ACTION
            completeTaskIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val completeTaskPendingIntent = PendingIntent.getBroadcast(
                    context, 0, completeTaskIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            widget_views.setPendingIntentTemplate(R.id.widget_listView, completeTaskPendingIntent)
            
            changeBackgroundColors(context, widget_views)
            
            //Set on widget_textview click open MainActivity.
            val configIntent = Intent(context, MainActivity::class.java)
            val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)
            widget_views.setOnClickPendingIntent(R.id.widget_textView, configPendingIntent)
            
            //Set on widget_settings_button open WidgetDialogActivity.
            val dialogIntent = Intent(context, WidgetSettingsActivity::class.java)
            dialogIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val dialogPendingIntent = PendingIntent.getActivity(context, 0, dialogIntent, 0)
            widget_views.setOnClickPendingIntent(R.id.widget_settings_button, dialogPendingIntent)
            
            //Set on widget_add_fab click open SetTaskActivity
            val openSetTaskActivityIntent = Intent(context, SetTaskActivity::class.java)
            val openSetTaskActivityPendingIntent =
                PendingIntent.getActivity(context, 0, openSetTaskActivityIntent, 0)
            widget_views.setOnClickPendingIntent(
                    R.id.widget_add_fab,
                    openSetTaskActivityPendingIntent)
            
            appWidgetManager.updateAppWidget(appWidgetId, widget_views)
            
        }
        
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action.equals(COMPLETE_TASK_ACTION)) {
            val id = intent.getIntExtra(EXTRA_ITEM, 0)
            
            val db = DBHelper(context)
            db.removeById(DBHelper.MAIN_TASKS_TABLE_NAME, id)
            sendRefreshBroadcast(context)
        }
        val action = intent.action
        if (!TextUtils.isEmpty(action)) {
            if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
                
                val manager = AppWidgetManager.getInstance(context)
                val cn = ComponentName(context, WidgetProvider::class.java)
                manager.notifyAppWidgetViewDataChanged(
                        manager.getAppWidgetIds(cn),
                        R.id.widget_listView
                )
                val appWidgetIds = manager.getAppWidgetIds(cn)
                val widget_views = RemoteViews(
                        context.packageName,
                        R.layout.widget
                )
                changeBackgroundColors(context, widget_views)
                
                manager.updateAppWidget(appWidgetIds, widget_views)
                
                
            }
            
        }
    }
    
    private fun changeBackgroundColors(context: Context, widget_views: RemoteViews) {
        //Here we set Layout's background and Settings button background color
        //to background color of the layout.
        val backgroundColor: Int?
        val colorPrimary: Int?
        val sp = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
        val themeIndex = sp.getInt("theme", WidgetSettingsActivity.appThemeIndex)
        when (themeIndex) {
            WidgetSettingsActivity.blueThemeIndex -> {
                backgroundColor = R.color.BlueTheme_Background
                colorPrimary = R.color.BlueTheme_colorPrimary
                
            }
            WidgetSettingsActivity.darkThemeIndex -> {
                backgroundColor = R.color.DarkTheme_Background
                colorPrimary = R.color.DarkTheme_colorPrimary
                
            }
            WidgetSettingsActivity.redThemeIndex -> {
                backgroundColor = R.color.RedTheme_Background
                colorPrimary = R.color.RedTheme_colorPrimary
                
            }
            WidgetSettingsActivity.appThemeIndex -> {
                backgroundColor = MySettings.getThemeColor(context, MySettings.BACKGROUND_COLOR)
                colorPrimary = MySettings.getThemeColor(context, MySettings.PRIMARY_COLOR)
            }
            else -> throw NullPointerException("value backgroundColor is null!")
            
        }
        widget_views.setInt(
                R.id.widget_actions_background,
                "setBackgroundResource",
                backgroundColor)
        widget_views.setInt(
                R.id.widget_background,
                "setBackgroundResource",
                backgroundColor)
        widget_views.setInt(
                R.id.widget_settings_button,
                "setBackgroundResource",
                backgroundColor)
        when (sp.getBoolean("fab_visible", true)){
            true ->{
                widget_views.setViewVisibility(R.id.widget_add_fab, View.VISIBLE)
                // Here we change tint color of button
                widget_views.setInt(
                        R.id.widget_add_fab,
                        "setColorFilter",
                        ContextCompat.getColor(context, backgroundColor))
            }
            false ->{
                widget_views.setViewVisibility(R.id.widget_add_fab, View.INVISIBLE)
    
            }
        }
        
    }
    
    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.setAction(action)
        
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }
    
    companion object {
        val EXTRA_ITEM = "EXTRA_ITEM"
        val COMPLETE_TASK_ACTION = "COMPLETE_TASK_ACTION"
        
        fun sendRefreshBroadcast(context: Context) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.component = ComponentName(context, WidgetProvider::class.java)
            context.sendBroadcast(intent)
            
        }
    }
}
