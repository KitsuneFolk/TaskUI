package com.pandacorp.taskui.presentation.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.data.repositories.TasksRepositoryImpl
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.util.Locale
import javax.inject.Inject

class WidgetFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: TasksRepositoryImpl
) : RemoteViewsService.RemoteViewsFactory {

    private val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.Widget.WIDGET_SETTINGS, Context.MODE_PRIVATE)
    }
    private var tasksList: MutableList<TaskItem> = mutableListOf()

    override fun onCreate() {}

    override fun getCount(): Int = tasksList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewAt(position: Int): RemoteViews {
        val taskItem = tasksList[position]
        val itemView = RemoteViews(context.packageName, R.layout.widget_task_item).apply {

            // Reset values
            setViewVisibility(R.id.widgetTimeTv, View.GONE)
            setViewVisibility(R.id.widgetPriorityImageView, View.GONE)

            setTextViewText(R.id.widgetTaskTitle, taskItem.text)
            taskItem.time?.let {
                setViewVisibility(R.id.widgetTimeTv, View.VISIBLE)
                setTextViewText(R.id.widgetTimeTv, dateFormatter.format(it))
            }
            // Set the priority
            taskItem.priority?.let {
                setViewVisibility(R.id.widgetPriorityImageView, View.VISIBLE)
                when (taskItem.priority) {
                    TaskItem.WHITE -> setImageViewResource(R.id.widgetPriorityImageView, R.color.white)
                    TaskItem.YELLOW -> setImageViewResource(R.id.widgetPriorityImageView, R.color.yellow)
                    TaskItem.RED -> setImageViewResource(R.id.widgetPriorityImageView, R.color.red)
                    else -> setViewVisibility(R.id.widgetPriorityImageView, View.GONE)
                }
            }

            // Handle the complete button click
            val fillInIntent = Intent().apply {
                putExtra(Constants.TaskItem.ID, taskItem.id)
                putExtra(Constants.TaskItem.TITLE, taskItem.text)
                putExtra(Constants.TaskItem.TIME, taskItem.time)
                putExtra(Constants.TaskItem.PRIORITY, taskItem.priority)
            }
            setOnClickFillInIntent(R.id.widgetCompleteButton, fillInIntent)

            setThemeColors(this, sp.getBoolean(Constants.Widget.IS_DARK_THEME, false))

        }

        return itemView
    }

    override fun getViewTypeCount(): Int = 1

    override fun hasStableIds(): Boolean = true

    override fun onDataSetChanged() {
        tasksList = repository.getAll().filter { it.status == TaskItem.MAIN }.toMutableList()
    }

    override fun onDestroy() {
        tasksList.clear()
    }

    private fun setThemeColors(remoteViews: RemoteViews, isDarkTheme: Boolean) {
        val backgroundColor: Int
        val accentColor: Int

        when (isDarkTheme) {
            true -> {
                backgroundColor = R.color.DarkTheme_colorBackground
                accentColor = R.color.DarkTheme_colorAccent
            }

            false -> {
                backgroundColor = R.color.BlueTheme_colorBackground
                accentColor = R.color.BlueTheme_colorAccent
            }
        }
        remoteViews.apply {
            setInt(
                R.id.widgetCompleteButton,
                "setBackgroundResource",
                backgroundColor
            )
            setInt(
                R.id.widgetCompleteButton,
                "setColorFilter",
                ContextCompat.getColor(context, accentColor)
            )
        }
    }
}