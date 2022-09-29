package com.pandacorp.taskui.Widget

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Switch
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.ui.settings.MySettings
import com.skydoves.powerspinner.PowerSpinnerView


class WidgetSettingsActivity : Activity() {
    private val TAG = "MyLogs"
    
    private var sp: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    
    companion object ThemeIndexes {
        val blueThemeIndex = 0
        val darkThemeIndex = 1
        val redThemeIndex = 2
        val appThemeIndex = 3
    }
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Without this line app is crashing.
        setResult(RESULT_CANCELED)
        sp = getSharedPreferences("widget_settings", Context.MODE_PRIVATE)!!
        edit = sp!!.edit()
        setContentView(R.layout.widget_settings_activity)
        setDialogTheme()
        initViews()
    }
    
    private fun setDialogTheme() {
        
        //Here we set colors for views in widget_settings_activity.
        val backgroundColor: Int?
        val themeIndex = sp!!.getInt("theme", appThemeIndex)
        when (themeIndex) {
            blueThemeIndex -> {
                backgroundColor = R.color.BlueTheme_Background
                
            }
            darkThemeIndex -> {
                backgroundColor = R.color.DarkTheme_Background
                
            }
            redThemeIndex -> {
                backgroundColor = R.color.RedTheme_Background
                
            }
            appThemeIndex -> {
                backgroundColor = MySettings.getThemeColor(this, MySettings.BACKGROUND_COLOR)
            }
            else -> throw NullPointerException("value backgroundColor is null!")
            
        }
        val widget_background = findViewById<LinearLayout>(R.id.widget_dialog_backgrund)
        widget_background.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        val theme_spinner = findViewById<PowerSpinnerView>(R.id.widget_settings_spinner_theme)
        theme_spinner.spinnerPopupBackground = ContextCompat.getDrawable(
                this,
                backgroundColor)
        
        
    }
    
    private fun initViews() {
        
        initThemeSpinner()
        initFABSwitch()
        
    }
    
    private fun initThemeSpinner() {
        val themeSpinner = findViewById<PowerSpinnerView>(R.id.widget_settings_spinner_theme)
        //This line needs for show selected value after open the spinner
        themeSpinner.selectItemByIndex(sp!!.getInt("theme", appThemeIndex))
        themeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            when (newIndex) {
                blueThemeIndex -> {
                    Log.d(TAG, "initViews: blue")
                    
                }
                darkThemeIndex -> {
                    Log.d(TAG, "initViews: dark")
                    
                }
                redThemeIndex -> {
                    Log.d(TAG, "initViews: red")
                    
                }
                appThemeIndex -> {
                    Log.d(TAG, "initViews: app")
                    
                }
            }
            changeWidgetTheme(newIndex)
        }
        themeSpinner.dividerColor = Color.WHITE
        
    }
    
    private fun initFABSwitch() {
        val widget_settings_show_fab_switch =
            findViewById<Switch>(R.id.widget_settings_show_fab_switch)
        widget_settings_show_fab_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    edit!!.putBoolean("fab_visible", true)
                    edit!!.apply()
                    
                    
                }
                false -> {
                    edit!!.putBoolean("fab_visible", false)
                    edit!!.apply()
                    
                }
                
            }
            WidgetProvider.sendRefreshBroadcast(this)
            
        }
        widget_settings_show_fab_switch.isChecked = sp!!.getBoolean("fab_visible", true)
        
    }
    
    private fun changeWidgetTheme(themeIndex: Int) {
        edit!!.putInt("theme", themeIndex)
        edit!!.apply()
        setDialogTheme()
        WidgetProvider.sendRefreshBroadcast(this)
    }
    
}