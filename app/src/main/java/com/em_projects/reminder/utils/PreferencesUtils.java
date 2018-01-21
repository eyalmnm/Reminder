package com.em_projects.reminder.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by eyalmuchtar on 1/21/18.
 */

public class PreferencesUtils {
    private static final String TAG = "PreferencesUtils";
    // Shared preferences file name
    private static final String PREF_NAME = "reminder";
    private static PreferencesUtils instance = null;
    // Shared preferences access components
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    // Application context
    private Context context;
    // Shared preferences working mode
    private int PRIVATE_MODE = 0;

    private PreferencesUtils(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public static PreferencesUtils getInstance(Context context) {
        if (null == instance) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    public boolean isFirstTime() {
        return preferences.getBoolean("first_time", true);
    }

    public void setFirstTime(boolean isFirstTime) {
        editor.putBoolean("first_time", isFirstTime);
        editor.commit();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        editor = null;
        preferences = null;
    }
}