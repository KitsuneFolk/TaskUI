package com.pandacorp.taskui.presentation.utils.dialog

import android.app.Activity
import android.os.Bundle
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.DialogListViewBinding
import com.pandacorp.taskui.presentation.ui.adapter.settings.SettingsAdapter
import com.pandacorp.taskui.presentation.ui.adapter.settings.SettingsItem
import com.pandacorp.taskui.presentation.utils.Constants

class DialogListView(private val activity: Activity, private val preferenceKey: String) : CustomDialog(activity) {
    private var _binding: DialogListViewBinding? = null
    private val binding get() = _binding!!

    private fun fillThemesList(): MutableList<SettingsItem> {
        val keysList = activity.resources.getStringArray(R.array.Themes_values)
        val titlesList = activity.resources.getStringArray(R.array.Themes)
        val itemsList =
            activity.resources.obtainTypedArray(R.array.Themes_drawables)
        val themesList: MutableList<SettingsItem> = mutableListOf()
        repeat(keysList.size) { i ->
            themesList.add(
                SettingsItem(
                    keysList[i],
                    titlesList[i],
                    itemsList.getDrawable(i)!!
                )
            )
        }
        itemsList.recycle()
        return themesList
    }

    private fun fillLanguagesList(): MutableList<SettingsItem> {
        val keysList = activity.resources.getStringArray(R.array.Languages_values)
        val drawablesList =
            activity.resources.obtainTypedArray(R.array.Languages_drawables)
        val titlesList = activity.resources.getStringArray(R.array.Languages)
        val itemsList: MutableList<SettingsItem> = mutableListOf()
        repeat(keysList.size) { i ->
            itemsList.add(
                SettingsItem(
                    keysList[i],
                    titlesList[i],
                    drawablesList.getDrawable(i)!!
                )
            )
        }
        drawablesList.recycle()
        return itemsList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DialogListViewBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.title.setText(
            when (preferenceKey) {
                Constants.PreferenceKeys.themesKey -> R.string.theme
                Constants.PreferenceKeys.languagesKey -> R.string.language
                else -> throw IllegalArgumentException("PreferenceKey = $preferenceKey")
            }
        )

        binding.cancel.setOnClickListener {
            cancel()
        }

        val itemsList: MutableList<SettingsItem> = when (preferenceKey) {
            Constants.PreferenceKeys.themesKey -> fillThemesList()
            Constants.PreferenceKeys.languagesKey -> fillLanguagesList()
            else -> throw IllegalArgumentException()

        }
        val adapter = SettingsAdapter(activity, itemsList, preferenceKey).apply {
            setOnClickListener { item ->
                sp.edit().putString(preferenceKey, item.value).apply()
                cancel()
                activity.apply {
                    recreate()
                    overridePendingTransition(0, 0)
                }
            }
        }
        binding.listView.adapter = adapter
    }
}