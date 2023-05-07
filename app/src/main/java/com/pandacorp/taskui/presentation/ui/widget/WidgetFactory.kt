package com.pandacorp.taskui.presentation.ui.widget

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

    companion object {
        const val TAG = "widget"
    }

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
        val itemView = RemoteViews(context.packageName, R.layout.widget_task_item)

        // Reset values
        itemView.setViewVisibility(R.id.widgetTimeTv, View.GONE)
        itemView.setViewVisibility(R.id.widgetPriorityImageView, View.GONE)

        itemView.setTextViewText(R.id.widgetTaskTitle, taskItem.text)
        taskItem.time?.let {
            itemView.setViewVisibility(R.id.widgetTimeTv, View.VISIBLE)
            itemView.setTextViewText(R.id.widgetTimeTv, dateFormatter.format(it))
        }
        taskItem.priority?.let {
            itemView.setViewVisibility(R.id.widgetPriorityImageView, View.VISIBLE)
            when (taskItem.priority) {
                TaskItem.WHITE -> itemView.setImageViewResource(R.id.widgetPriorityImageView, R.color.white)
                TaskItem.YELLOW -> itemView.setImageViewResource(R.id.widgetPriorityImageView, R.color.yellow)
                TaskItem.RED -> itemView.setImageViewResource(R.id.widgetPriorityImageView, R.color.red)
                else -> itemView.setViewVisibility(R.id.widgetPriorityImageView, View.GONE)
            }
        }

        setThemeColors(itemView, sp.getBoolean(Constants.Widget.IS_DARK_THEME, false))

        // Handle the complete button click
        val fillInIntent = Intent().apply {
            putExtra(Constants.Widget.ITEM, taskItem)
        }
        itemView.setOnClickFillInIntent(R.id.widgetCompleteButton, fillInIntent)

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
        remoteViews.setInt(
            R.id.widgetCompleteButton,
            "setBackgroundResource",
            backgroundColor
        )
        remoteViews.setInt(
            R.id.widgetCompleteButton,
            "setColorFilter",
            ContextCompat.getColor(context, accentColor)
        )
    }
}