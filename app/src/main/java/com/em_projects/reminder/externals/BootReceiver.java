package com.em_projects.reminder.externals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by eyalmuchtar on 12/28/17.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Toast.makeText(context, "Booting Completed", Toast.LENGTH_LONG).show();
        Intent serviceIntent = new Intent(context, ReminderAlarmManagerService.class);
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(serviceIntent);
    }

}
