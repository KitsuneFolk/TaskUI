package com.pandacorp.taskui.presentation.ui.settings

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.ActivitySettingsBinding
import com.pandacorp.taskui.presentation.ui.settings.dialogs.DialogListView
import com.pandacorp.taskui.presentation.utils.Constants
import com.pandacorp.taskui.presentation.utils.PreferenceHandler
import com.pandacorp.taskui.presentation.utils.getPackageInfoCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var sp: SharedPreferences

    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceHandler(this).load()
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setTitle(R.string.settings)
        sp = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var sp: SharedPreferences
        private var themesListPreference: ListPreference? = null
        private var languagesListPreference: ListPreference? = null
        private var versionPreference: Preference? = null
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            themesListPreference = findPreference(Constants.PreferencesKeys.themesKey)
            languagesListPreference = findPreference(Constants.PreferencesKeys.languagesKey)
            versionPreference = findPreference(Constants.PreferencesKeys.versionKey)
            // Here we add version title to this preference, we get version from build.gradle file.
            try {
                versionPreference!!.title =
                    resources.getString(R.string.version) + " " + requireContext().packageManager
                        .getPackageInfoCompat(requireContext().packageName).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            when (preference.key) {
                Constants.PreferencesKeys.themesKey -> // rounded themes dialog with images
                    DialogListView.newInstance(Constants.PreferencesKeys.themesKey)
                        .show(parentFragmentManager, null)

                Constants.PreferencesKeys.languagesKey -> // rounded languages dialog with images
                    DialogListView.newInstance(Constants.PreferencesKeys.languagesKey)
                        .show(parentFragmentManager, null)

                else -> {
                    super.onDisplayPreferenceDialog(preference)
                }
            }

        }

    }

    companion object {
        const val TAG = "SettingsActivity"
    }
}