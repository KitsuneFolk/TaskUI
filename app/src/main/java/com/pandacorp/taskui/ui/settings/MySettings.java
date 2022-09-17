package com.pandacorp.taskui.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import com.pandacorp.taskui.R;

import java.util.Locale;

public class MySettings {
    final private String TAG = "MyLogs";
    private Context context;

    private String theme;
    private String language;

    private final SharedPreferences sp;

    public final static String Theme_Blue = "blue";
    public final static String Theme_Dark = "dark";
    public final static String Theme_Red = "red";

    public MySettings(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void load() {
        //Обработка при первом запуске приложения, когда SharedPreferences ещё не созданы.

        theme = sp.getString("Themes", Theme_Blue);
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

        switch (language) {
            case "ru": {
                Locale russian_locale = new Locale("ru");
                Locale.setDefault(russian_locale);
                Configuration configuration = new Configuration();
                configuration.locale = russian_locale;
                context.getResources().updateConfiguration(configuration, null);

                break;
            }
            case "en": {
                Locale english_locale = new Locale("en");
                Locale.setDefault(english_locale);
                Configuration configuration = new Configuration();
                configuration.locale = english_locale;
                context.getResources().updateConfiguration(configuration, null);


                break;
            }
            case "uk": {
                Locale ukrainian_locale = new Locale("uk");
                Locale.setDefault(ukrainian_locale);
                Configuration configuration = new Configuration();
                configuration.locale = ukrainian_locale;
                context.getResources().updateConfiguration(configuration, null);


                break;
            }
        }
    }

    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final String ACCENT_COLOR = "ACCENT_COLOR";

    public static int getThemeColor(Context context, String color) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int background_color;
        int accent_color;
        switch (sp.getString("Themes", "blue")) {
            case Theme_Blue: {
                background_color = R.color.BlueTheme_Background;
                accent_color = R.color.BlueTheme_colorAccent;
                break;

            }
            case Theme_Dark: {
                background_color = R.color.DarkTheme_Background;
                accent_color = R.color.DarkTheme_colorAccent;
                break;

            }
            case Theme_Red: {
                background_color = R.color.RedTheme_Background;
                accent_color = R.color.RedTheme_colorAccent;
                break;

            }

            default:
                throw new IllegalStateException("Unexpected value: " + sp.getString("Themes", "blue"));
        }
        if (color.equals(BACKGROUND_COLOR)) return background_color;
        if (color.equals(ACCENT_COLOR)) return accent_color;

        return -1;
    }


}
