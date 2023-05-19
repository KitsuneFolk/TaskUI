package com.pandacorp.taskui.presentation.di.app

import android.app.Application
import com.pandacorp.taskui.R
import com.pandacorp.taskui.domain.models.TaskItem
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    var selectedNavigationItemId = R.id.nav_main_tasks
    // A TaskItem object to pass from AddTaskScreen back to MainTasksFragment
    var taskItem: TaskItem ?= null
}