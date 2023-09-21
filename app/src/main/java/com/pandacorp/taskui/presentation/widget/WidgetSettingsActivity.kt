package com.pandacorp.taskui.presentation.widget

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.WidgetSettingsActivityBinding
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.Utils

class WidgetSettingsActivity : Activity() {
    private val sp: SharedPreferences by lazy {
        getSharedPreferences(Constants.Widget.WIDGET_SETTINGS, Context.MODE_PRIVATE)
    }
    private val edit: SharedPreferences.Editor by lazy {
        sp.edit()!!
    }

    private var _binding: WidgetSettingsActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setupExceptionHandler()

        // Set the theme and get the background id, setTheme mandatory before the binding
        val background: Int = when (sp.getBoolean(Constants.Widget.IS_DARK_THEME, false)) {
            true -> {
                setTheme(R.style.DarkTheme_Widget_MaterialAlertDialogStyle)
                R.drawable.rounded_dark_background
            }

            false -> {
                setTheme(R.style.BlueTheme_Widget_MaterialAlertDialogStyle)
                R.drawable.rounded_blue_background
            }
        }
        _binding = WidgetSettingsActivityBinding.inflate(layoutInflater)

        // Without this line the app crashes
        setResult(RESULT_CANCELED)

        setContentView(binding.root)
        binding.widgetDialogBackground.background = ContextCompat.getDrawable(this, background)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Remove background to show the custom one
        initViews()
    }

    private fun initViews() {
        binding.apply {
            showAddButtonSwitch.apply {
                isChecked = sp.getBoolean(Constants.Widget.IS_FAB_VISIBLE, true)
            }
            showDarkThemeSwitch.apply {
                isChecked = sp.getBoolean(Constants.Widget.IS_DARK_THEME, false)
            }
            widgetSettingsOk.apply {
                setOnClickListener {
                    edit.apply {
                        putBoolean(
                            Constants.Widget.IS_FAB_VISIBLE,
                            showAddButtonSwitch.isChecked,
                        )
                        putBoolean(
                            Constants.Widget.IS_DARK_THEME,
                            showDarkThemeSwitch.isChecked,
                        )
                        commit()
                    }
                    WidgetProvider.update(this@WidgetSettingsActivity)
                    finish()
                }
            }
            widgetSettingsCancel.apply {
                setOnClickListener {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}