package com.pandacorp.taskui.Widget

import android.content.Intent
import android.widget.RemoteViewsService


class WidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return WidgetFactory(applicationContext, intent)
        
    }
    
}