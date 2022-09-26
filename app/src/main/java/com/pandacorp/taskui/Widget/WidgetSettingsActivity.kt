package com.pandacorp.taskui.Widget

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.pandacorp.taskui.R
import com.pandacorp.taskui.ui.settings.MySettings
import com.skydoves.powerspinner.PowerSpinnerView


class WidgetSettingsActivity : Activity() {
    private val TAG = "MyLogs"
    
    private var sp: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    
    companion object ThemeIndexes{
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
        val theme_spinner = findViewById<PowerSpinnerView>(R.id.widget_settings_spinner_theme)
        //This line needs for show selected value after open the
        theme_spinner.selectItemByIndex(sp!!.getInt("theme", appThemeIndex))
        theme_spinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
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
        
    }
    
    private fun changeWidgetTheme(themeIndex: Int) {
        edit!!.putInt("theme", themeIndex)
        edit!!.apply()
        setDialogTheme()
        WidgetProvider.sendRefreshBroadcast(this)
    }
    
}