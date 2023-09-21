package com.pandacorp.taskui.presentation.utils

sealed class Constants {
    object PreferenceKeys {
        const val languagesKey = "Languages"
        const val themesKey = "Themes"

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
        const val COMPLETE_TASK_ACTION = "com.pandacorp.taskui.COMPLETE_TASK_ACTION"
        const val SET_TASK_ACTION = "com.pandacorp.taskui.SET_TASK_ACTION"
        const val IS_FAB_VISIBLE = "fab_visible"
        const val IS_DARK_THEME = "IS_DARK_THEME"
    }

    object Notification {
        const val CHANNEL_KEY = "taskui_to_do_list"
        const val ACTION_CREATE = "com.pandacorp.taskui.ACTION_CREATE_NOTIFICATION"
        const val ACTION_COMPLETE = "com.pandacorp.taskui.ACTION_COMPLETE_NOTIFICATION"
    }

    object TaskItem {
        const val ID = "ID"
        const val PRIORITY = "PRIORITY"
        const val TITLE = "TITLE"
        const val TIME = "TIME"
    }

    object Fragment {
        const val Action = "Action"
        const val ADD_TASK = "com.pandacorp.taskui.ADD_TASK"
    }

    companion object {
        // Key for intent to put and get a serializable item
        const val IntentItem = "IntentItem"

        const val ANIMATION_DURATION = 500L

        // Position to add items in list, add the new item at start
        const val ITEM_ADD_POSITION = 0
    }
}