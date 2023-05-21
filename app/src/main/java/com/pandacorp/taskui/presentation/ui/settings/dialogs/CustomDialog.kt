package com.pandacorp.taskui.presentation.ui.settings.dialogs

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager

abstract class CustomDialog : DialogFragment() {
    protected val sp: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        requireDialog().window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // remove default background so that dialog can be rounded
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // remove shadow
        }
        super.onViewCreated(view, savedInstanceState)
    }
}