package com.pandacorp.taskui.presentation.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WidgetService : RemoteViewsService() {
    @Inject
    lateinit var widgetFactory: WidgetFactory
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        widgetFactory
}