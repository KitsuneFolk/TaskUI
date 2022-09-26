package com.pandacorp.taskui.Widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.Adapter.ListItem
import com.pandacorp.taskui.DBHelper
import com.pandacorp.taskui.R
import com.pandacorp.taskui.ui.settings.MySettings


class WidgetFactory internal constructor(
    private val context: Context,
    private val intent: Intent?
) :
    RemoteViewsService.RemoteViewsFactory {
    private val TAG = "MyLogs"
    
    private lateinit var db: DBHelper
    private lateinit var wdb: SQLiteDatabase
    private lateinit var cursor: Cursor
    
    private var tasks = ArrayList<ListItem>()
    private var widgetID = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
    
    override fun onCreate() {
        
        getDatabaseItems()
        
    }
    
    private fun getDatabaseItems() {
        
        db = DBHelper(context)
        
        //Creating WritableDatabase object
        wdb = db.writableDatabase
        
        //Creating Cursor object
        cursor = wdb.query(DBHelper.MAIN_TASKS_TABLE_NAME, null, null, null, null, null, null)
        
        //Uploading the timers when opening the app
        val TEXT_COL = cursor.getColumnIndex(DBHelper.KEY_TASK_TEXT)
        val TIME_COL = cursor.getColumnIndex(DBHelper.KEY_TASK_TIME)
        val PRIORITY_COL = cursor.getColumnIndex(DBHelper.KEY_TASK_PRIORITY)
        if (cursor.moveToFirst()) {
            do {
                val item = ListItem(
                        cursor.getString(TEXT_COL),
                        cursor.getString(TIME_COL),
                        cursor.getString(PRIORITY_COL),
                        
                        )
                
                tasks.add(item)
                
                
            } while (cursor.moveToNext())
        }
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
    
        val widget_item_view = RemoteViews(context.packageName, R.layout.widget_list_item)
    
    
        //Here we set Tasks text.
        widget_item_view.setTextViewText(R.id.widget_main_textView, tasks[position].mainText)
        widget_item_view.setTextViewText(R.id.widget_time_textview, tasks[position].time)
        
        
        //Here we set Tasks priority to ImageView.
        when (tasks[position].priority) {
            ListItem.white -> {
                widget_item_view.setImageViewResource(R.id.widget_priority_imageview, R.color.mdtp_white)
                
            }
            ListItem.yellow -> {
                widget_item_view.setImageViewResource(R.id.widget_priority_imageview, R.color.Yellow)
                
            }
            ListItem.red -> {
                widget_item_view.setImageViewResource(R.id.widget_priority_imageview, R.color.Red)
                
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
        //Here we set Layout's widget_item_background, widget_complete_button background, widget_complete_button tint.
        val sp = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)!!
    
        val themeIndex = sp.getInt("theme", WidgetSettingsActivity.appThemeIndex)
        val backgroundColor: Int?
        val accentColor: Int?
        
        when (themeIndex) {
            WidgetSettingsActivity.blueThemeIndex -> {
                backgroundColor = R.color.BlueTheme_Background
                accentColor = R.color.BlueTheme_colorAccent
            
            }
            WidgetSettingsActivity.darkThemeIndex -> {
                backgroundColor = R.color.DarkTheme_Background
                accentColor = R.color.DarkTheme_colorAccent
            
            }
            WidgetSettingsActivity.redThemeIndex -> {
                backgroundColor = R.color.RedTheme_Background
                accentColor = R.color.RedTheme_colorAccent
            
            }
            WidgetSettingsActivity.appThemeIndex -> {
                backgroundColor = MySettings.getThemeColor(context, MySettings.BACKGROUND_COLOR)
                accentColor = MySettings.getThemeColor(context, MySettings.ACCENT_COLOR)
            }
            else -> throw NullPointerException("value backgroundColor is null!")
        
        }
        widget_item_view.setInt(
                R.id.widget_item_background,
                "setBackgroundResource",
                backgroundColor)
        widget_item_view.setInt(
                R.id.widget_complete_button,
                "setBackgroundResource",
                backgroundColor)
        widget_item_view.setInt(
                R.id.widget_complete_button,
                "setColorFilter",
                ContextCompat.getColor(context, accentColor!!))
    
    
    }
    
    override fun getViewTypeCount(): Int {
        return 1
    }
    
    override fun hasStableIds(): Boolean {
        return true
    }
    
    override fun onDataSetChanged() {
        tasks.clear()
        getDatabaseItems()
    }
    
    override fun onDestroy() {}
    
    
}