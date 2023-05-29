package com.pandacorp.taskui.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.pandacorp.taskui.R
import com.pandacorp.taskui.data.repositories.TasksRepositoryImpl
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.ui.activity.MainActivity
import com.pandacorp.taskui.presentation.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class WidgetProvider : AppWidgetProvider() {
    companion object {
        fun update(context: Context) {
            val appWidgetIds =
                AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))

            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                component = ComponentName(context, WidgetProvider::class.java)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }

    @Inject
    lateinit var repository: TasksRepositoryImpl

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Constants.Widget.COMPLETE_TASK_ACTION -> {
                // Get the task from intent
                intent.apply {
                    val time = getLongExtra(Constants.TaskItem.TIME, Long.MIN_VALUE)
                    val priority = getIntExtra(Constants.TaskItem.PRIORITY, Int.MIN_VALUE)
                    val taskItem = TaskItem(
                        getLongExtra(Constants.TaskItem.ID, Long.MIN_VALUE),
                        getStringExtra(Constants.TaskItem.TITLE)!!,
                        if (time == Long.MIN_VALUE) null else time,
                        if (priority == Int.MIN_VALUE) null else priority,
                        TaskItem.COMPLETED
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.update(taskItem)
                        withContext(Dispatchers.Main) {
                            val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
                            widgetManager.notifyAppWidgetViewDataChanged(
                                widgetManager.getAppWidgetIds(
                                    ComponentName(
                                        context.applicationContext.packageName,
                                        WidgetProvider::class.java.name
                                    )
                                ), R.id.widget_listView
                            )
                        }
                    }
                    // Send broadcast receiver to complete the item in MainTasksViewModel
                    val updateVmValueIntent = Intent(Constants.Widget.COMPLETE_TASK_ACTION).apply {
                        putExtra(Constants.IntentItem, taskItem)
                    }
                    context.sendBroadcast(updateVmValueIntent)
                }
            }

            Constants.Widget.SET_TASK_ACTION -> {
                val i = Intent(context, MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    action = Constants.Fragment.ADD_TASK
                }
                context.startActivity(i)
            }

            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
                val cn = ComponentName(context, WidgetProvider::class.java)
                widgetManager.notifyAppWidgetViewDataChanged(
                    widgetManager.getAppWidgetIds(cn),
                    R.id.widget_listView
                )
                val appWidgetIds = widgetManager.getAppWidgetIds(cn)
                val widgetViews = RemoteViews(context.packageName, R.layout.widget)
                setWidgetStyle(context, widgetViews)

                widgetManager.updateAppWidget(appWidgetIds, widgetViews)
            }
        }
    }

    private fun updateWidget(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
    ) {
        val widgetViews = RemoteViews(context.packageName, R.layout.widget)

        val adapterIntent = Intent(context, WidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }
        // On widgetAddButton click complete the task
        val completeTaskIntent = Intent(context, WidgetProvider::class.java).apply {
            action = Constants.Widget.COMPLETE_TASK_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val completeTaskPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            completeTaskIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Open MainActivity on widget_textview click .
        val openMainActivityIntent = Intent(context, MainActivity::class.java)
        val openMainActivityPendingIntent = PendingIntent.getActivity(
            context, 1, openMainActivityIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Open WidgetSettingsActivity on widget_settings_button click.
        val openSettingsActivityIntent = Intent(context, WidgetSettingsActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val openSettingsActivityPendingIntent = PendingIntent.getActivity(
            context, 2, openSettingsActivityIntent, PendingIntent.FLAG_IMMUTABLE
        )
        // Open SetTaskActivity on widget_add_fab click
        val openSetTaskActivityIntent = Intent(context, WidgetProvider::class.java).apply {
            action = Constants.Widget.SET_TASK_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        // Close all running activities to not handle case when user can open MainActivity and tasks wouldn't be updated in the viewmodel.
        val openSetTaskActivityPendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            openSetTaskActivityIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        widgetViews.apply {

            setRemoteAdapter(R.id.widget_listView, adapterIntent)
            setPendingIntentTemplate(R.id.widget_listView, completeTaskPendingIntent)
            setOnClickPendingIntent(R.id.widgetCompleteButton, completeTaskPendingIntent)

            setOnClickPendingIntent(R.id.widget_textView, openMainActivityPendingIntent)
            setOnClickPendingIntent(R.id.widget_settings_button, openSettingsActivityPendingIntent)
            setOnClickPendingIntent(R.id.widgetAddButton, openSetTaskActivityPendingIntent)

            setWidgetStyle(context, this)

            appWidgetManager.updateAppWidget(appWidgetId, this)
        }
    }

    /**
     * Sets the colors and transparency for the widget, based on the user's selected theme and transparency level.
     * @param context The context of the application.
     * @param widgetViews The RemoteViews object representing the widget.
     */
    private fun setWidgetStyle(context: Context, widgetViews: RemoteViews) {
        val sp =
            context.getSharedPreferences(Constants.Widget.WIDGET_SETTINGS, Context.MODE_PRIVATE)
        val isDarkTheme = sp.getBoolean(Constants.Widget.IS_DARK_THEME, false)

        val backgroundColorResId = if (isDarkTheme) {
            R.color.DarkTheme_colorBackground
        } else {
            R.color.BlueTheme_colorBackground
        }

        val backgroundDrawableResId = if (isDarkTheme) {
            R.drawable.rounded_dark_background
        } else {
            R.drawable.rounded_blue_background
        }
        val addButtonDrawableResId = if (isDarkTheme) {
            R.drawable.widget_darktheme_circle_button
        } else {
            R.drawable.widget_bluetheme_circle_button
        }

        widgetViews.apply {
            setInt(R.id.widget_actions_background, "setBackgroundResource", backgroundColorResId)
            setInt(R.id.widgetBackground, "setBackgroundResource", backgroundDrawableResId)
            setInt(R.id.widget_settings_button, "setBackgroundResource", backgroundColorResId)
            setInt(R.id.widgetAddButton, "setBackgroundResource", addButtonDrawableResId)

            val fabVisibility =
                if (sp.getBoolean(Constants.Widget.IS_FAB_VISIBLE, true)) View.VISIBLE
                else View.GONE
            setViewVisibility(R.id.widgetAddButton, fabVisibility)
        }
    }
}
