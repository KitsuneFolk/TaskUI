package com.pandacorp.taskui.presentation.di.app

import android.app.Application
import com.pandacorp.taskui.domain.models.TaskItem
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    // TaskItem object to pass from AddTaskScreen back to MainTasksFragment
    var taskItem: TaskItem? = null
}