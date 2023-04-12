package com.pandacorp.taskui.presentation.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.pandacorp.taskui.R
import com.pandacorp.taskui.domain.models.TaskItem


class WidgetFactory internal constructor(
    private val context: Context,
    private val intent: Intent?
) :
    RemoteViewsService.RemoteViewsFactory {
    private val TAG = "MyLogs"
    
    private var tasks = ArrayList<TaskItem>()
    private var widgetID = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
    
    override fun onCreate() {
        
    }
    
    override fun getCount(): Int {
        return tasks.size
    }
    
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    
    override fun getLoadingView(): RemoteViews? {
        
        return null
        
    }
    
    override fun getViewAt(position: Int): RemoteViews {
    
        val widget_item_view = RemoteViews(context.packageName, R.layout.widget_task_item)
    
    
        //Here we set Tasks text.
        widget_item_view.setTextViewText(R.id.widget_main_textView, tasks[position].text)
        widget_item_view.setTextViewText(R.id.widget_time_textview, tasks[position].time)
        
        
        //Here we set Tasks priority to ImageView.
        when (tasks[position].priority) {
            TaskItem.WHITE -> {
                widget_item_view.setImageViewResource(R.id.widget_priority_imageview, android.R.color.white)
                
            }
            TaskItem.YELLOW -> {
                widget_item_view.setImageViewResource(R.id.widget_priority_imageview, R.color.yellow)
                
            }
            TaskItem.RED -> {
                widget_item_view.setImageViewResource(R.id.widget_priority_imageview, R.color.red)
                
            }
            else -> {
                //Null variation
            }
        }
        
        setBackgroundColors(context, widget_item_view)
        
        //Code needed for handling the complete button in widget list item.
        val extras = Bundle()
        extras.putInt(WidgetProvider.EXTRA_ITEM, position)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        widget_item_view.setOnClickFillInIntent(R.id.widget_complete_button, fillInIntent)
        
        return widget_item_view
    }
    
    private fun setBackgroundColors(context: Context, widget_item_view: RemoteViews){
        // //Here we set Layout's widget_item_background, widget_complete_button background, widget_complete_button tint.
        // val sp = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)!!
        //
        // val themeIndex = sp.getInt("theme", WidgetSettingsActivity.appThemeIndex)
        // val backgroundColor: Int?
        // val accentColor: Int?
        //
        // when (themeIndex) {
        //     WidgetSettingsActivity.blueThemeIndex -> {
        //         backgroundColor = R.colors.BlueTheme_Background
        //         accentColor = R.colors.BlueTheme_colorAccent
        //
        //     }
        //     WidgetSettingsActivity.darkThemeIndex -> {
        //         backgroundColor = R.colors.DarkTheme_Background
        //         accentColor = R.colors.DarkTheme_colorAccent
        //
        //     }
        //     WidgetSettingsActivity.redThemeIndex -> {
        //         backgroundColor = R.colors.RedTheme_Background
        //         accentColor = R.colors.RedTheme_colorAccent
        //
        //     }
        //     WidgetSettingsActivity.appThemeIndex -> {
        //         backgroundColor = MySettings.getThemeColor(context, MySettings.BACKGROUND_COLOR)
        //         accentColor = MySettings.getThemeColor(context, MySettings.ACCENT_COLOR)
        //     }
        //     else -> throw NullPointerException("value backgroundColor is null!")
        //
        // }
        // widget_item_view.setInt(
        //         R.id.widget_item_background,
        //         "setBackgroundResource",
        //         backgroundColor)
        // widget_item_view.setInt(
        //         R.id.widget_complete_button,
        //         "setBackgroundResource",
        //         backgroundColor)
        // widget_item_view.setInt(
        //         R.id.widget_complete_button,
        //         "setColorFilter",
        //         ContextCompat.getColor(context, accentColor!!))
        //
        //
    }
    
    override fun getViewTypeCount(): Int {
        return 1
    }
    
    override fun hasStableIds(): Boolean {
        return true
    }
    
    override fun onDataSetChanged() {
        tasks.clear()
    }
    
    override fun onDestroy() {}
    
    
}