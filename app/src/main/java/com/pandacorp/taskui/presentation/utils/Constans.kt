package com.pandacorp.taskui.presentation.utils

sealed class Constans {
    object PreferencesKeys {
        const val languagesKey = "Languages"
        const val themesKey = "Themes"
        const val versionKey = "Version"
        
        const val preferenceBundleKey = "preferenceBundleKey"
    }
    
    // Item touch helper sealed class with keys to detect what adapter's viewholder use.
    sealed class ITHKey {
        object MAIN : ITHKey()
        object COMPLETED : ITHKey()
        object DELETED : ITHKey()
    }
    
    object Room {
        const val NAME = "myDatabase"
    }
    
    object FragmentKey {
        const val MAIN = 0
        const val COMPLETED = 1
        const val DELETED = 2
    }
    
    companion object {
        // bundle key if value is stored in isolation and no need to create other keys
        const val valueKey = "valueKey"
        
        // Key for intent to put and get serializable item
        const val IntentItem = "IntentItem"
        
        // Key for intent to put and get item's position
        const val IntentItemPosition = "IntentItemPosition"
        
        const val SNACKBAR_DURATION = 3000
        
        // Delay to closing NavigationView after an item was selected
        const val CLOSE_DELAY = 300L
    }
}