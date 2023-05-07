package com.pandacorp.taskui.presentation.utils

sealed class Constants {
    object PreferencesKeys {
        const val languagesKey = "Languages"
        const val themesKey = "Themes"
        const val versionKey = "Version"
        
        const val preferenceBundleKey = "preferenceBundleKey"
    }
    
    // Item touch helper sealed class with keys to detect which adapter's viewHolder use.
    sealed class ITHKey {
        object MAIN : ITHKey()
        object COMPLETED : ITHKey()
        object DELETED : ITHKey()
    }
    
    object Room {
        const val NAME = "myDatabase"
    }
    object Widget {
        const val WIDGET_SETTINGS = "widget_settings"
        const val ITEM = "ITEM"
        const val COMPLETE_TASK_ACTION = "COMPLETE_TASK_ACTION"
        const val SET_TASK_ACTION = "SET_TASK_ACTION"
        const val IS_FAB_VISIBLE = "fab_visible"
        const val IS_DARK_THEME = "IS_DARK_THEME"

        const val IS_FROM_WIDGET = "IS_FROM_WIDGET"
    }
    object Notification {
        const val CHANNEL_KEY = "taskui_to_do_list"
        const val ACTION_CREATE = "com.pandacorp.taskui.ACTION_CREATE_NOTIFICATION"
        const val ACTION_COMPLETE = "com.pandacorp.taskui.ACTION_COMPLETE_NOTIFICATION"
    }
    companion object {
        const val PRIORITY = "PRIORITY"
        const val TITLE = "TITLE"
        const val TIME = "TIME"

        // Key for intent to put and get a serializable item
        const val IntentItem = "IntentItem"
        // Key for intent to put and get an item's position
        const val IntentItemPosition = "IntentItemPosition"

        const val ANIMATION_DURATION = 500L

        // Position to add items in list, add the new item at start
        const val ITEM_ADD_POSITION = 0
    }
}