package com.em_projects.reminder;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.reminder.loaders.LoaderFromAssets;
import com.em_projects.reminder.model.Event;
import com.em_projects.reminder.storage.db.DbConstants;
import com.em_projects.reminder.ui.AnimationCreator;
import com.em_projects.reminder.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by eyal muchtar on 12/2/17.
 */

// Ref: https://stackoverflow.com/questions/27473245/how-to-play-ringtone-sound-in-android-with-infinite-loop
// Ref: https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
// Ref: http://code4reference.com/2012/07/tutorial-on-android-alarmmanager/
// Ref: https://stackoverflow.com/questions/13820596/start-android-service-after-every-5-minutes
// Ref: https://stackoverflow.com/questions/3629179/android-activity-over-default-lock-screen
// Ref: https://www.bignerdranch.com/blog/frame-animations-in-android/
// Ref: https://code.tutsplus.com/tutorials/quick-tip-customize-android-fonts--mobile-1601
// Ref: http://stacktips.com/tutorials/android/repeat-alarm-example-in-android   <-------<<-

public class FloatingLayoutService extends Service {

    private static final String TAG = "FloatingLayoutService";

    private int x;
    private int y;

    private WindowManager mWindowManager;
    private View mFloatingWidget;
    private View collapsedView;
    private TextView mainSubjectTextView;
    private ImageView animImageView;
    private View expandedView;
    private TextView subjectTextView;

    private Point displaySize = new Point();

    private ValueAnimator translator;

    private Intent intent;
    private MediaPlayer mediaPlayer;

    private final static int MINUTE_MILIS = 60 * 1000;


    // Thread's properties
    private WindowManager.LayoutParams params;

    public FloatingLayoutService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        // Start Foreground service
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "remainder_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Reminder Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        }

        // Init UI
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        if (Build.VERSION.SDK_INT == 26) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSPARENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSPARENT);
        }
        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_layout, null);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);

        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        x = size.x;
        y = size.y;
        Log.d(TAG, "onCreate size.x: " + String.valueOf(x));

        collapsedView = mFloatingWidget.findViewById(R.id.collapse_view);
        mainSubjectTextView = mFloatingWidget.findViewById(R.id.mainSubjectTextView);
        mainSubjectTextView.setText("");
        animImageView = mFloatingWidget.findViewById(R.id.animImageView);
        ((AnimationDrawable) animImageView.getBackground()).start();
        animImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "collapsed_iv onClick");
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                if (null != translator) {
                    translator.cancel();
                }
                stopMedia();
                int viewHeight = mFloatingWidget.getHeight() / 2;
                int viewWidth = dpToPx(150) / 2;
                params.y = y / 2 - viewHeight;
                params.x = x / 2 - viewWidth;
                mWindowManager.updateViewLayout(mFloatingWidget, params);
            }
        });
        expandedView = mFloatingWidget.findViewById(R.id.expanded_container);
        subjectTextView = mFloatingWidget.findViewById(R.id.subjectTextView);
        ImageView closeButton = mFloatingWidget.findViewById(R.id.closeButton);
        ImageView snoozeButton = mFloatingWidget.findViewById(R.id.snoozeButton);
        subjectTextView.setText("");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "closeButton");
                selfStop();
            }
        });
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "snoozeButton");
                setOnetimeTimer(intent);
                selfStop();
            }
        });
    }

    private void stopMedia() {
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if ((null == this.intent) ||
                (!this.intent.getStringExtra(DbConstants.EVENTS_ID).equalsIgnoreCase(intent.getStringExtra(DbConstants.EVENTS_ID)))) {
            long now = System.currentTimeMillis();
            this.intent = intent;
            if (now >= intent.getLongExtra(DbConstants.EVENTS_START_DATE, 0)) {
                Log.d(TAG, "onStartCommand - Setting new Start Date");
                long nextEvent = getNextStartDate(intent);
                intent.putExtra(DbConstants.EVENTS_START_DATE, nextEvent);
                setOnetimeTimer(intent, true);
            }
            String subject = this.intent.getStringExtra(DbConstants.EVENTS_SUBJECT);
            if (StringUtils.isNullOrEmpty(subject)) subject = "Empty Subject";
            mainSubjectTextView.setText(subject);
            mainSubjectTextView.setSelected(true);
            subjectTextView.setText(subject);

            String animationName = intent.getStringExtra(DbConstants.EVENTS_ANIMATION_NAME);
            ArrayList<Drawable> drawables = null;
            try {
                drawables = LoaderFromAssets.getImagesByDir(this, animationName);
                AnimationDrawable animDrawable = AnimationCreator.createAnimationDrawable(drawables, 50, false);
                AnimationCreator.playAnimation(animImageView, animDrawable);
            } catch (IOException e) {
                Log.e(TAG, "onViewCreated", e);
                Toast.makeText(this, "Failed to play animation", Toast.LENGTH_LONG).show();
            }
            String ringTone = intent.getStringExtra(DbConstants.EVENTS_TUNE_NAME);
            if (!"silence".equalsIgnoreCase(ringTone)) {
                startMedia(ringTone);
            }

            animate(mFloatingWidget, this.intent, 0, x, y / 2, y / 2);
        }

        return Service.START_NOT_STICKY;
    }

    private void startMedia(String ringTone) {
        if (!StringUtils.isNullOrEmpty(ringTone)) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(ringTone));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private long getNextStartDate(Intent intent) {
        Log.d(TAG, "getNextStartDate");
        Event.RepeatType repeatType = Event.getRepeatType(intent.getStringExtra(DbConstants.EVENTS_REPEAT_TYPE));
        long startTime = intent.getLongExtra(DbConstants.EVENTS_START_DATE, System.currentTimeMillis());
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
        return calendar.getTimeInMillis();
    }

    public void setOnetimeTimer(Intent dataIntent) {
        Log.d(TAG, "setOnetimeTimer the short");
        setOnetimeTimer(dataIntent, false);
    }

    public void setOnetimeTimer(Intent dataIntent, boolean setNextAlert) {
        if (!this.intent.getBooleanExtra("isPreview", false)) {
            Log.d(TAG, "setOnetimeTimer");
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent eventIntent = new Intent(this, FloatingLayoutService.class);
            eventIntent.putExtras(Objects.requireNonNull(dataIntent.getExtras()));
            PendingIntent pi = PendingIntent.getService(this, 0, eventIntent, 0);
            long interval = dataIntent.getLongExtra(DbConstants.EVENTS_ALERTS_INTERVAL, MINUTE_MILIS);
            Log.d(TAG, "setOnetimeTimer interval: " + interval);
            long nextTime = System.currentTimeMillis() + interval;  // Next interval time
            if (setNextAlert) { // Next time event time
                nextTime = dataIntent.getLongExtra(DbConstants.EVENTS_START_DATE, MINUTE_MILIS);
            }
            am.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
        }
    }

    private boolean isViewCollapsed() {
        return mFloatingWidget == null || mFloatingWidget.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    public void animate(final View view, final Intent intent, int startX, final int endX, int startY, int endY) {
        Log.d(TAG, "animate startX: " + startX + " endX: " + endX);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", startX, endX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", startY, endY);

        translator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY);

        translator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int viewHeight = mFloatingWidget.getHeight() / 2;
                int collapsed_iv_width = animImageView.getWidth();

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                layoutParams.x = (Integer) valueAnimator.getAnimatedValue("x");
                layoutParams.y = (Integer) valueAnimator.getAnimatedValue("y") - viewHeight;
//                Log.d(TAG, "onAnimationUpdate - update location x: " + layoutParams.x);

                try {
                    mWindowManager.updateViewLayout(view, layoutParams);
                    if ((endX - collapsed_iv_width) <= layoutParams.x) {
                        Log.d(TAG, "onAnimationUpdate - update location ending ");
                        translator.cancel();
                        setOnetimeTimer(intent);
                        FloatingLayoutService.this.selfStop();
                    }
                } catch (Throwable tr) {
                    Log.e(TAG, "onAnimationUpdate x: " + params.x, tr);
                    translator.cancel();
                }
            }
        });
        translator.setDuration(5000);
        translator.start();
    }

    public void selfStop() {
        Log.d(TAG, "selfStop");
        if (null != translator) translator.cancel();
        stopMedia();
        stopForeground(true);
        stopSelf();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (null != translator) translator.cancel();
        if (mFloatingWidget != null) mWindowManager.removeView(mFloatingWidget);
    }
}
