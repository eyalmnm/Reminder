package com.em_projects.reminder;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.em_projects.reminder.model.Event;
import com.em_projects.reminder.storage.db.DbConstants;
import com.em_projects.reminder.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by eyalmuchtar on 12/2/17.
 */

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
    private ImageView animImageView;
    private View expandedView;
    private TextView subjectTextView;
    private ImageView closeButton;
    private ImageView snoozeButton;

    private boolean isPreview;
    private ValueAnimator translator;

    private Intent intent;

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
        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_layout, null);

//        params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);

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

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);

        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        x = size.x;
        y = size.y;
        Log.d(TAG, "onAnimationUpdate end x: " + String.valueOf(x));

        collapsedView = mFloatingWidget.findViewById(R.id.collapse_view);
        animImageView = mFloatingWidget.findViewById(R.id.animImageView);
        ((AnimationDrawable) animImageView.getBackground()).start();
//        Animation rotateAnimation = AnimationUtils.loadAnimation(FloatingLayoutService.this, R.anim.rotation_anim);
        animImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "collapsed_iv onClick");
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                if (null != translator) {
                    translator.cancel();
                }
                int viewHeight = mFloatingWidget.getHeight() / 2;
                int viewWidth = dpToPx(150) / 2;
                params.y = y / 2 - viewHeight;
                params.x = x / 2 - viewWidth;
                mWindowManager.updateViewLayout(mFloatingWidget, params);
            }
        });
        expandedView = mFloatingWidget.findViewById(R.id.expanded_container);
        subjectTextView = mFloatingWidget.findViewById(R.id.subjectTextView);
        closeButton = (ImageView) mFloatingWidget.findViewById(R.id.closeButton);
        snoozeButton = (ImageView) mFloatingWidget.findViewById(R.id.snoozeButton);
        subjectTextView.setText("");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (false == isPreview)setOnetimeTimer(intent);
                stopSelf();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long now = System.currentTimeMillis();
        if (now >= intent.getLongExtra(DbConstants.EVENTS_START_DATE, 0)) {
            long nextEvent = getNextStartDate(intent);
            intent.putExtra(DbConstants.EVENTS_START_DATE, nextEvent);
            setOnetimeTimer(intent, true);
        }
        this.intent = intent;
        String subject = this.intent.getStringExtra("subject");
        if (true == StringUtils.isNullOrEmpty(subject)) subject = "Empty Subject";
        subjectTextView.setText(subject);
        isPreview = this.intent.getBooleanExtra("isPreview", false);

        animate(mFloatingWidget, this.intent, 0, x, y / 2, y / 2);

        return super.onStartCommand(intent, flags, startId);
    }

    private long getNextStartDate(Intent intent) {
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
        setOnetimeTimer(dataIntent, false);
    }

    public void setOnetimeTimer(Intent dataIntent, boolean setNextAlert) {  // TODO Use AlarmManagerHelper
        Log.d(TAG, "setOnetimeTimer");
        int sessionRepeated = dataIntent.getIntExtra("sessionRepeated", -1);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent eventIntent = new Intent(this, FloatingLayoutService.class);
        eventIntent.getExtras().putAll(dataIntent.getExtras());
        PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
        long nextTime = System.currentTimeMillis() + dataIntent.getLongExtra(DbConstants.EVENTS_ALERTS_INTERVAL, MINUTE_MILIS);  // Next interval time
        if (setNextAlert) { // Next time event time
            nextTime = dataIntent.getLongExtra(DbConstants.EVENTS_START_DATE, MINUTE_MILIS);
        }
        am.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
    }

    private boolean isViewCollapsed() {
        return mFloatingWidget == null || mFloatingWidget.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    public void animate(final View v, final Intent intent, int startX, final int endX, int startY, int endY) {

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", startX, endX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", startY, endY);

        translator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY);

        translator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int viewHeight = mFloatingWidget.getHeight() / 2;
                int collapsed_iv_width = animImageView.getWidth();

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) v.getLayoutParams();
                layoutParams.x = (Integer) valueAnimator.getAnimatedValue("x");
                layoutParams.y = (Integer) valueAnimator.getAnimatedValue("y") - viewHeight;

                try {
                    mWindowManager.updateViewLayout(v, layoutParams);
                    if ((endX - collapsed_iv_width) <= layoutParams.x) {
                        setOnetimeTimer(intent);
                        FloatingLayoutService.this.stopSelf();
                    }
                } catch (Throwable tr) {
                    Log.e(TAG, "onAnimationUpdate", tr);
                }
            }
        });
        translator.setDuration(5000);
        translator.start();
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
