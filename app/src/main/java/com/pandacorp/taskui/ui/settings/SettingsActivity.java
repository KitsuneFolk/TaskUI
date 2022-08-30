package com.pandacorp.taskui.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.pandacorp.taskui.MainActivity;
import com.pandacorp.taskui.R;

public class SettingsActivity extends AppCompatActivity {
    final String TAG = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MySettings mySettings = new MySettings(this);
        mySettings.load();
        setContentView(R.layout.fragment_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new SettingsFragment())
                .commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //Метод обработки нажатия на кнопку home.
                startActivity(new Intent(this, MainActivity.class));
                finish();

                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        String language;
        String theme;

        SharedPreferences sp;

        ListPreference themes_listPreference;
        ListPreference languages_listPreference;
        Preference version_Preference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            sp = PreferenceManager.getDefaultSharedPreferences(getContext());

            themes_listPreference = findPreference("Themes");
            themes_listPreference.setOnPreferenceChangeListener(this);

            languages_listPreference = findPreference("Languages");
            languages_listPreference.setOnPreferenceChangeListener(this);

            version_Preference = findPreference("Version");

            String version_name;
            try {
                version_name = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
                version_Preference.setTitle(getResources().getString(R.string.version) + " " + version_name);
                //Тут происходит добавление загаловка в виде версии к пункту настроек.

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String TAG = "MyLogs";
            Log.d(TAG, "onPreferenceChange: preference = " + preference.getKey());
            Log.d(TAG, "onPreferenceChange: newValue = " + newValue);

            language = sp.getString("Languages", "");
            theme = sp.getString("Themes", "");

            switch (preference.getKey()) {
                case "Themes":

                    getActivity().recreate();
                    break;
                case "Languages":

                    getActivity().recreate();
                    break;
                default:

            }

            return true;
        }


    }


}