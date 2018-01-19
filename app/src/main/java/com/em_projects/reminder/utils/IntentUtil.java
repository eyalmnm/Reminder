package com.em_projects.reminder.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

/**
 * Created by eyalmuchtar on 1/18/18.
 */

public class IntentUtil {

    public static void printIntentData(Intent intent, String tag) {
        if (false == StringUtils.isNullOrEmpty(tag)) {
            if (23 <= tag.length()) throw new RuntimeException("TAG must contains less then 23 letters");
            if (null != intent && null != intent.getExtras()) {
                Bundle data = intent.getExtras();
                Set<String> keys = data.keySet();
                for (String key : keys) {
                    Log.d(tag, key + " = " + data.get(key));
                }
            }
        }
    }
}
