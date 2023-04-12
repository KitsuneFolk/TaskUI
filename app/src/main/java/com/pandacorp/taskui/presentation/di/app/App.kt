package com.pandacorp.taskui.presentation.di.app

import android.app.Application
import com.pandacorp.taskui.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    var selectedNavigationItemId = R.id.nav_main_tasks
}