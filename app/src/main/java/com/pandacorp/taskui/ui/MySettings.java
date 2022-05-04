package com.pandacorp.taskui.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import com.pandacorp.taskui.R;

import java.util.Locale;

public class MySettings {
    final private String TAG = "MyLogs";
    Context context;

    private String theme;
    private String language;

    private SharedPreferences sp;

    public MySettings(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void load() {
        //Обработка при первом запуске приложения, когда SharedPreferences ещё не созданы.

        theme = sp.getString("Themes", "blue");
        language = sp.getString("Languages", "");
        setMyTheme();

        setMyLanguage();


    }

    private void setMyTheme() {
        switch (theme) {
            case "blue":

                context.setTheme(R.style.BlueTheme);

                break;
            case "dark":
                context.setTheme(R.style.DarkTheme);

                break;
            case "red":
                context.setTheme(R.style.RedTheme);

                break;
        }


    }

    private void setMyLanguage() {

        if (language.equals("ru")) {
            Locale russian_locale = new Locale("ru");
            Locale.setDefault(russian_locale);
            Configuration configuration = new Configuration();
            configuration.locale = russian_locale;
            context.getResources().updateConfiguration(configuration, null);

        } else if (language.equals("en")) {
            Locale english_locale = new Locale("en");
            Locale.setDefault(english_locale);
            Configuration configuration = new Configuration();
            configuration.locale = english_locale;
            context.getResources().updateConfiguration(configuration, null);


        } else if (language.equals("uk")) {
            Locale ukrainian_locale = new Locale("uk");
            Locale.setDefault(ukrainian_locale);
            Configuration configuration = new Configuration();
            configuration.locale = ukrainian_locale;
            context.getResources().updateConfiguration(configuration, null);


        }
    }

}
