package com.em_projects.reminder.externals;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.em_projects.reminder.FloatingLayoutService;
import com.em_projects.reminder.alarm_mngr.AlarmManagerHelper;
import com.em_projects.reminder.model.Event;
import com.em_projects.reminder.storage.db.DbConstants;
import com.em_projects.reminder.storage.db.EventsDbHandler;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by eyalmuchtar on 12/29/17.
 */

// Ref: http://stacktips.com/tutorials/android/repeat-alarm-example-in-android

public class ReminderAlarmManagerService extends Service {
    private static final String TAG = "RemainderAlarmMngrSrv";

    private EventsDbHandler dbHandler;

    private Context context;

    private Handler serviceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        dbHandler = EventsDbHandler.getInstance(this);
        initHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        setAlarms();
        return START_STICKY;
    }

    private void initHandler() {
        HandlerThread thread = new HandlerThread("OpenWeatherServiceThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Thread's properties
        Looper serviceLopper = thread.getLooper();
        serviceHandler = new Handler(serviceLopper);
    }

    // Try to reset the alarm manger or use for debug only  // TODO
    private void setAlarms() {
        serviceHandler.post(new Runnable() {
            @Override
            public void run() {

                ArrayList<Event> events = dbHandler.getAllByStartDate();
                if (null != events && 0 < events.size()) {

                    long now = System.currentTimeMillis();
                    long startTime, duration;
                    Event.RepeatType repeatType;

                    for (Event event : events) {
                        startTime = event.getStartDate();
                        duration = event.getDuration();
                        repeatType = Event.getRepeatType(event.getRepeatType());

                        if (Event.RepeatType.NONE == repeatType && now > (startTime + duration /*+ (numberOfAlerts * alertsInterval)*/)) {
                            dbHandler.deleteEvent(event);
                            continue;
                        }

                        // Set current alert time
                        if (now < startTime) {
                            Log.d(TAG, "setOnetimeTimer");
                            Intent intent = createIntentFromEvent(context, event);
                            registerAlert(context, intent, Integer.parseInt(event.getId()));
                        }

                        // Set alert to next time
                        if (now > startTime) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(startTime);
                            switch (repeatType) {
                                case DAILY:
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    break;
                                case WEEKLY:
                                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                                    break;
                                case MONTHLY:
                                    calendar.add(Calendar.MONTH, 1);
                                    break;
                                case YEARLY:
                                    calendar.add(Calendar.YEAR, 1);
                                    break;
                            }
                            event.setStartDate(calendar.getTimeInMillis());
                            dbHandler.updateEvent(event);
                            Intent intent = createIntentFromEvent(context, event);
                            registerAlert(context, intent, Integer.parseInt(event.getId()));
                        }
                    }
                }
            }
        });
    }

    public static Intent createIntentFromEvent(Context context, Event event) {
        Intent intent = new Intent(context, FloatingLayoutService.class);

        intent.putExtra(DbConstants.EVENTS_ID, event.getId());
        intent.putExtra(DbConstants.EVENTS_SUBJECT, event.getSubject());
        intent.putExtra(DbConstants.EVENTS_START_DATE, event.getStartDate());
        intent.putExtra(DbConstants.EVENTS_DURATION, event.getDuration());
        intent.putExtra(DbConstants.EVENT_ALARM_SECONDS_BEFORE, event.getTimeBeforeSec());
//        intent.putExtra(DbConstants.EVENTS_NUMBER_OF_ALERTS, event.getNumberOfAlerts());
        intent.putExtra(DbConstants.EVENTS_ALERTS_INTERVAL, event.getAlertsInterval());
        intent.putExtra(DbConstants.EVENTS_ALERTS_INTERVAL, event.getAlertsInterval());
        intent.putExtra(DbConstants.EVENTS_REPEAT_TYPE, event.getRepeatType());
        intent.putExtra(DbConstants.EVENTS_ANIMATION_NAME, event.getAnimationName());
        intent.putExtra(DbConstants.EVENTS_TUNE_NAME, event.getTuneName());
        intent.putExtra("isPreview", false);

        return intent;
    }

    private void registerAlert(Context context, Intent intent, int requestId) {
        AlarmManagerHelper.registerAlert(context, intent, requestId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
