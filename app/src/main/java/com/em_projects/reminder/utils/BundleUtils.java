package com.em_projects.reminder.utils;

import android.os.Bundle;
import android.util.Log;

import java.util.Set;

/**
 * Created by eyalmuchtar on 1/6/18.
 */

public class BundleUtils {
    private static final String TAG = "BundleUtils";

    public static void printContent(String tag, Bundle bundle) {
        Set<String> keys = bundle.keySet();
        for (String key: keys) {
            Log.d(tag, "key: " + key + " value: " + bundle.get(key));
        }
    }
}
