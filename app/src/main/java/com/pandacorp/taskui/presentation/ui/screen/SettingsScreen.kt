package com.pandacorp.taskui.presentation.ui.screen

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ScreenSettingsBinding
import com.pandacorp.taskui.presentation.ui.settings.dialogs.DialogListView
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.getPackageInfoCompat
import com.pandacorp.taskui.presentation.utils.supportActionBar

class SettingsScreen : Fragment(R.layout.screen_settings) {
    companion object {
        const val TAG = "SettingsScreen"
    }

    private var _binding: ScreenSettingsBinding? = null
    private val binding get() = _binding!!

    private val sp: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    private fun initViews() {
        // Retrieve the version from build.gradle and assign it to the TextView
        try {
            val version = requireContext().packageManager.getPackageInfoCompat(requireContext().packageName).versionName
            val stringVersion = resources.getString(R.string.version, version)
            binding.versionTextView.text = stringVersion
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.themeLayout.apply {
            setOnClickListener {
                DialogListView.newInstance(Constants.PreferenceKeys.themesKey)
                    .show(parentFragmentManager, null)
            }
        }
        binding.languageLayout.apply {
            setOnClickListener {
                DialogListView.newInstance(Constants.PreferenceKeys.languagesKey)
                    .show(parentFragmentManager, null)
            }
        }
        binding.themeTextView.apply {
            val themeKey = sp.getString(
                Constants.PreferenceKeys.themesKey,
                requireContext().resources.getString(R.string.settings_theme_default_value)
            )!!
            text = getThemeFromKey(themeKey)
        }
        binding.languageTextView.apply {
            val languageKey = sp.getString(
                Constants.PreferenceKeys.languagesKey,
                requireContext().resources.getString(R.string.settings_language_default_value)
            )!!
            text = getLanguageFromKey(languageKey)
        }
    }
    private fun getThemeFromKey(key: String): String {
        val keys = resources.getStringArray(R.array.Themes)
        val values = resources.getStringArray(R.array.Themes_values)

        val index = values.indexOf(key)
        return if (index != -1 && index < keys.size) {
            keys[index]
        } else {
            ""
        }
    }
    private fun getLanguageFromKey(key: String): String {
        val keys = resources.getStringArray(R.array.Languages)
        val values = resources.getStringArray(R.array.Languages_values)

        val index = values.indexOf(key)
        return if (index != -1 && index < keys.size) {
            keys[index]
        } else {
            ""
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ScreenSettingsBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        supportActionBar?.setTitle(R.string.settings)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}