package com.em_projects.reminder.alarm_mngr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.em_projects.reminder.storage.db.DbConstants;

/**
 * Created by eyalmuchtar on 1/4/18.
 */

public class AlarmManagerHelper {

    public static void registerAlert(Context context, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        long startTime = intent.getLongExtra(DbConstants.EVENTS_START_DATE, System.currentTimeMillis());
        am.set(AlarmManager.RTC_WAKEUP, startTime, pi);
    }

}
