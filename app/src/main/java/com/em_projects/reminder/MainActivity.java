package com.em_projects.reminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.reminder.alarm_mngr.AlarmManagerHelper;
import com.em_projects.reminder.alerts_data.AlertsListActivity;
import com.em_projects.reminder.dialogs.AnimationPreviewDialog;
import com.em_projects.reminder.externals.ReminderAlarmManagerService;
import com.em_projects.reminder.fragments.DatePickerDialog;
import com.em_projects.reminder.fragments.TimePickerDialog;
import com.em_projects.reminder.model.Event;
import com.em_projects.reminder.storage.db.DbConstants;
import com.em_projects.reminder.storage.db.EventsDbHandler;
import com.em_projects.reminder.ui.custom_text.CustomButton;
import com.em_projects.reminder.ui.custom_text.CustomCheckBox;
import com.em_projects.reminder.ui.custom_text.CustomEditText;
import com.em_projects.reminder.ui.custom_text.CustomRadioButton;
import com.em_projects.reminder.ui.custom_text.CustomTextView;
import com.em_projects.reminder.ui.widgets.ringtonepicker.RingtonePickerDialog;
import com.em_projects.reminder.ui.widgets.ringtonepicker.RingtonePickerListener;
import com.em_projects.reminder.utils.PreferencesUtils;
import com.em_projects.reminder.utils.StringUtils;
import com.em_projects.reminder.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;

// Ref: https://www.spaceotechnologies.com/android-floating-widget-tutorial/   <--------------<<<-
// Ref: https://stackoverflow.com/questions/38531778/how-to-translate-windowmanager-floating-layout-diagonally-with-animation-chath
// Ref: https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
// Ref: https://www.sitepoint.com/animating-android-floating-action-button/
// Ref: http://stacktips.com/tutorials/android/repeat-alarm-example-in-android

public class MainActivity extends AppCompatActivity implements
        DatePickerDialog.OnDatePickedListener,
        TimePickerDialog.OnTimePickedListener {

    private static final String TAG = "MainActivity";

    private static final int PERM_REQUEST_CODE_DRAW_OVERLAYS = 123;
    private static final int PERMISSION_REQUEST_CODE = 456;

    private CustomEditText subjectEditText;
    private ImageButton showAlertsImageButton;

    private CustomTextView dateTextView;
    private CustomTextView timeTextView;
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private int hour = 0;
    private int minute = 0;

    private Spinner eventDurationSpinner;
    private CustomCheckBox durationInHoursIndicator;
    private Spinner eventHoursDurationSpinner;
    private SpinnerAdapter eventDurationSpinnerAdapter;
    private SpinnerAdapter eventDurationInHoursSpinnerAdapter;
    private static final long MINUTE_MILLIS = 60 * 1000;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int MINUTES_15 = 0;
    private static final int MINUTES_30 = 1;
    private static final int MINUTES_60 = 2;
    private static final int MINUTES_120 = 3;
    private static final int WHOLE_DAY = 4;
    private static final int HOURS_2_5 = 0;
    private static final int HOURS_3_0 = 1;
    private static final int HOURS_3_5 = 2;
    private static final int HOURS_4_0 = 3;
    private static final int HOURS_4_5 = 4;
    private static final int HOURS_5_0 = 5;
    private static final int HOURS_5_5 = 6;
    private static final int HOURS_6_0 = 7;
    private static final int HOURS_6_5 = 8;
    private static final int HOURS_7_0 = 9;
    private static final int HOURS_7_5 = 10;
    private static final int HOURS_8_0 = 11;
    private static final int HOURS_8_5 = 12;
    private static final int HOURS_9_0 = 13;
    private static final int HOURS_9_5 = 14;
    private static final int HOURS_10_0 = 15;
    private static final int HOURS_10_5 = 16;
    private static final int HOURS_11_0 = 17;
    private static final int HOURS_11_5 = 18;
    private static final int HOURS_12_0 = 19;
    private boolean wholeDay = false;
    private long duration = 0;
    private long durationInHours = 0;

    private Spinner eventRepeatSpinner;
    private SpinnerAdapter eventRepeatSpinnerAdapter;
    private Event.RepeatType repeatType = Event.RepeatType.NONE;
    private static final int REPEAT_ONE_TIME = 0;
    private static final int REPEAT_DAILY = 1;
    private static final int REPEAT_WEEKLY = 2;
    private static final int REPEAT_MONTHLY = 3;
    private static final int REPEAT_YEARLY = 4;

    private Spinner eventTimeBeforeSpinner;
    private SpinnerAdapter eventTimeBeforeSpinnerAdapter;
    private long eventTimeBeforeSec;

    private Spinner animationSelectionSpinner;
    private CustomTextView animationPreviewButton;
    private SpinnerAdapter animationSelectionSpinnerAdapter;
    private String animationName;

    private RadioGroup alertRepeatOptionsRadioGroup;
    private CustomRadioButton everyMinuteRadioButton;
    private CustomRadioButton everyThreeMinuteRadioButton;
    private CustomRadioButton everyFiveMinuteRadioButton;
    private long alertsInterval;

    private CustomCheckBox addSoundIndicator;
    private CustomTextView soundSelectionTextView;
    private String tuneName;

    private CustomButton saveButton;
    private CustomButton cancelButton;

    private Context context;

    private EventsDbHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        subjectEditText = findViewById(R.id.subjectEditText);
        showAlertsImageButton = findViewById(R.id.showAlertsImageButton);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);

        eventDurationSpinner = findViewById(R.id.eventDurationSpinner);
        durationInHoursIndicator = findViewById(R.id.durationInHoursIndicator);
        eventHoursDurationSpinner = findViewById(R.id.eventHoursDurationSpinner);

        eventRepeatSpinner = findViewById(R.id.eventRepeatSpinner);

        eventTimeBeforeSpinner = findViewById(R.id.eventTimeBeforeSpinner);

        animationSelectionSpinner = findViewById(R.id.animationSelectionSpinner);
        animationPreviewButton = findViewById(R.id.animationPreviewButton);

        alertRepeatOptionsRadioGroup = findViewById(R.id.alertRepeatOptionsRadioGroup);
        everyMinuteRadioButton = findViewById(R.id.everyMinuteRadioButton);
        everyThreeMinuteRadioButton = findViewById(R.id.everyThreeMinuteRadioButton);
        everyFiveMinuteRadioButton = findViewById(R.id.everyFiveMinuteRadioButton);
        everyFiveMinuteRadioButton.setChecked(true);
        alertsInterval = 5 * MINUTE_MILLIS;

        addSoundIndicator = findViewById(R.id.addSoundIndicator);
        soundSelectionTextView = findViewById(R.id.soundSelectionTextView);
        soundSelectionTextView.setVisibility(View.INVISIBLE);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                requestPermission();
            } else {
                continueAppLoading();
            }
        } else {
            continueAppLoading();
        }
    }

    private void continueAppLoading() {
        initializeView();
        dbHandler = EventsDbHandler.getInstance(this);
    }

    private void initAlarms() {
        Intent intent = new Intent(context, AlertsListActivity.class);
        startActivity(intent);
    }

    private void initializeView() {
        long currentTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        dateTextView.setText(TimeUtils.getDateStr(currentTime));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
        timeTextView.setText(TimeUtils.getTimeStr(currentTime));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });

        Resources resource = getResources();
        ArrayList<String> durationOptions = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.duration_options)));
        eventDurationSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, durationOptions);
        eventDurationSpinner.setAdapter(eventDurationSpinnerAdapter);
        eventDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeTextView.setEnabled(true);
                wholeDay = false;
                switch (position) {
                    case MINUTES_15:
                        duration = 15 * MINUTE_MILLIS;
                        break;
                    case MINUTES_30:
                        duration = 30 * MINUTE_MILLIS;
                        break;
                    case MINUTES_60:
                        duration = 1 * HOUR_MILLIS;
                        break;
                    case MINUTES_120:
                        duration = 2 * HOUR_MILLIS;
                        break;
                    case WHOLE_DAY:
                        duration = 24 * HOUR_MILLIS;
                        timeTextView.setEnabled(false);
                        wholeDay = true;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });
        durationInHoursIndicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    eventHoursDurationSpinner.setVisibility(View.VISIBLE);
                } else {
                    durationInHours = 0;
                    eventHoursDurationSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });
        eventHoursDurationSpinner.setVisibility(View.INVISIBLE);
        final ArrayList<String> durationInHoursOptions = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.duration_in_hours_options)));
        eventDurationInHoursSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, durationInHoursOptions);
        eventHoursDurationSpinner.setAdapter(eventDurationInHoursSpinnerAdapter);
        eventHoursDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case HOURS_2_5:
                        durationInHours = (long) (2.5 * HOUR_MILLIS);
                        break;
                    case HOURS_3_0:
                        durationInHours = 3 * HOUR_MILLIS;
                        break;
                    case HOURS_3_5:
                        durationInHours = (long) (3.5 * HOUR_MILLIS);
                        break;
                    case HOURS_4_0:
                        durationInHours = 4 * HOUR_MILLIS;
                        break;
                    case HOURS_4_5:
                        durationInHours = (long) (4.5 * HOUR_MILLIS);
                        break;
                    case HOURS_5_0:
                        durationInHours = 5 * HOUR_MILLIS;
                        break;
                    case HOURS_5_5:
                        durationInHours = (long) (5.5 * HOUR_MILLIS);
                        break;
                    case HOURS_6_0:
                        durationInHours = 6 * HOUR_MILLIS;
                        break;
                    case HOURS_6_5:
                        durationInHours = (long) (6.5 * HOUR_MILLIS);
                        break;
                    case HOURS_7_0:
                        durationInHours = 7 * HOUR_MILLIS;
                        break;
                    case HOURS_7_5:
                        durationInHours = (long) (7.5 * HOUR_MILLIS);
                        break;
                    case HOURS_8_0:
                        durationInHours = 8 * HOUR_MILLIS;
                        break;
                    case HOURS_8_5:
                        durationInHours = (long) (8.5 * HOUR_MILLIS);
                        break;
                    case HOURS_9_0:
                        durationInHours = 9 * HOUR_MILLIS;
                        break;
                    case HOURS_9_5:
                        durationInHours = (long) (9.5 * HOUR_MILLIS);
                        break;
                    case HOURS_10_0:
                        durationInHours = 10 * HOUR_MILLIS;
                        break;
                    case HOURS_10_5:
                        durationInHours = (long) (10.5 * HOUR_MILLIS);
                        break;
                    case HOURS_11_0:
                        durationInHours = 11 * HOUR_MILLIS;
                        break;
                    case HOURS_11_5:
                        durationInHours = (long) (11.5 * HOUR_MILLIS);
                        break;
                    case HOURS_12_0:
                        durationInHours = 12 * HOUR_MILLIS;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });

        ArrayList<String> repeatOptins = generateRepeatOptions();
        eventRepeatSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, repeatOptins);
        eventRepeatSpinner.setAdapter(eventRepeatSpinnerAdapter);
        eventRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case REPEAT_ONE_TIME:
                        repeatType = Event.RepeatType.NONE;
                        break;
                    case REPEAT_DAILY:
                        repeatType = Event.RepeatType.DAILY;
                        break;
                    case REPEAT_WEEKLY:
                        repeatType = Event.RepeatType.WEEKLY;
                        break;
                    case REPEAT_MONTHLY:
                        repeatType = Event.RepeatType.MONTHLY;
                        break;
                    case REPEAT_YEARLY:
                        repeatType = Event.RepeatType.YEARLY;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });

        ArrayList<String> timeBeforeOptions = generateTimeBeforeOptions();
        eventTimeBeforeSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, timeBeforeOptions);
        eventTimeBeforeSpinner.setAdapter(eventTimeBeforeSpinnerAdapter);
        eventTimeBeforeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        eventTimeBeforeSec = 0; // On Time
                        break;
                    case 1:
                        eventTimeBeforeSec = 60; // 1 Min before
                        break;
                    case 3:
                        eventTimeBeforeSec = 120; // 2 Min before
                        break;
                    case 4:
                        eventTimeBeforeSec = 300; // 5 Min before
                        break;
                    case 5:
                        eventTimeBeforeSec = 600; // 10 Min before
                        break;
                    case 6:
                        eventTimeBeforeSec = 900; // 15 Min before
                        break;
                    case 7:
                        eventTimeBeforeSec = 1800; // 30 Min before
                        break;
                    case 8:
                        eventTimeBeforeSec = 3600; // 1 Hour before
                        break;
                    case 9:
                        eventTimeBeforeSec = 7200; // 2 Hours before
                        break;
                        default:
                            eventTimeBeforeSec = 0;
                            break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });

        final ArrayList<String> animationOptions = generateAnimationOptions();
        animationSelectionSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, animationOptions);
        animationSelectionSpinner.setAdapter(animationSelectionSpinnerAdapter);
        animationSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                animationName = animationOptions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });
        animationPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //runAnimation(true);
                showAnimationPreviewDialog(animationName);
            }
        });
        animationName = animationOptions.get(0);

        addSoundIndicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    soundSelectionTextView.setVisibility(View.VISIBLE);
                } else {
                    soundSelectionTextView.setVisibility(View.INVISIBLE);
                    soundSelectionTextView.setText(null);
                    tuneName = null;
                }
            }
        });
        soundSelectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRingTonePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //runAnimation(false);
                String id = String.valueOf(System.currentTimeMillis());
                String subject = subjectEditText.getText().toString();
                long eventDuration = duration;
                if (true == StringUtils.isNullOrEmpty(subject)) {
                    Toast.makeText(context, R.string.empty_subject, Toast.LENGTH_LONG).show();
                    return;
                }
                if (0 < durationInHours && durationInHoursIndicator.isChecked()) {
                    eventDuration = durationInHours;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                if (wholeDay) {
                    calendar.set(year, month, day, 0, 0);
                } else {
                    calendar.set(year, month, day, hour, minute);
                    if (0 < eventTimeBeforeSec) {
                        calendar.add(Calendar.SECOND, (int)(-1 * eventTimeBeforeSec));
                    }
                }
                long now = System.currentTimeMillis();
                long startDate = calendar.getTimeInMillis();
                if (0 > (startDate - now)) {
                    Toast.makeText(context, R.string.event_starts_in_past, Toast.LENGTH_LONG).show();
                    return;
                }
                String calendarTime = TimeUtils.timeToFormatedString(calendar.getTimeInMillis());
                Log.d(TAG, "Time in calendar is: " + calendarTime);
                int radioButtonId = alertRepeatOptionsRadioGroup.getCheckedRadioButtonId();
                if (R.id.everyThreeMinuteRadioButton == radioButtonId) {
                    alertsInterval = 3 * MINUTE_MILLIS;
                } else if (R.id.everyFiveMinuteRadioButton == radioButtonId) {
                    alertsInterval = 5 * MINUTE_MILLIS;
                } else {
                    alertsInterval = 1 * MINUTE_MILLIS;
                }
                Event event = new Event(id, subject, startDate, eventDuration, repeatType,
                        animationName, /*numberOfAlerts,*/ alertsInterval, tuneName);
                dbHandler.addEvent(event);
                addToAlarmManager(context, event);
                addToAlarmManager(context, event);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //runAnimation(false);
                finish();
            }
        });

        if (true == PreferencesUtils.getInstance(context).isFirstTime()) {
            showAlertsImageButton.setVisibility(View.INVISIBLE);
            PreferencesUtils.getInstance(context).setFirstTime(false);
        } else {
            showAlertsImageButton.setVisibility(View.VISIBLE);
        }
        showAlertsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAlarms();
            }
        });
    }

    private void showRingTonePicker() {
        RingtonePickerDialog.Builder ringtonePickerBuilder =
                new RingtonePickerDialog.Builder(MainActivity.this, getSupportFragmentManager());

        //Set title of the dialog.
        ringtonePickerBuilder.setTitle("Select ringtone");

        //Add the desirable ringtone types.
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);

        //set the text to display of the positive (ok) button. (Optional)
        ringtonePickerBuilder.setPositiveButtonText("SET RINGTONE");

        //set text to display as negative button. (Optional)
        ringtonePickerBuilder.setCancelButtonText("CANCEL");

        //Set flag true if you want to play the com.ringtonepicker.sample of the clicked tone.
        ringtonePickerBuilder.setPlaySampleWhileSelection(true);

        //Set the callback listener.
        ringtonePickerBuilder.setListener(new RingtonePickerListener() {
            @Override
            public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                tuneName = ringtoneUri.toString();
                soundSelectionTextView.setText(ringtoneName);
            }
        });

        //set the currently selected uri, to mark that ringtone as checked by default. (Optional)
        //ringtonePickerBuilder.setCurrentRingtoneUri(mCurrentSelectedUri);

        //Display the dialog.
        ringtonePickerBuilder.show();
    }

    private void addToAlarmManager(Context context, Event event) {
        Intent intent = ReminderAlarmManagerService.createIntentFromEvent(context, event);
        AlarmManagerHelper.registerAlert(context, intent);
    }

//    private ArrayList<String> generateSoundsOption() {
//        ArrayList<String> soundOptions = new ArrayList<>();
//        soundOptions.add("silence");
//        soundOptions.addAll(getRingTones());
//        return soundOptions;
//    }

//    private void runAnimation(boolean isPreview) {
//        Intent intent = new Intent(this, FloatingLayoutService.class);
//        String subject = subjectEditText.getText().toString();
//        if (true == StringUtils.isNullOrEmpty(subject)) subject = "Empty Subject";
//        intent.putExtra(DbConstants.EVENTS_SUBJECT, subject);
//        intent.putExtra("isPreview", isPreview);
//        intent.putExtra(DbConstants.EVENTS_ANIMATION_NAME, animationName);
//        //startService(intent);
//    }

    private void showAnimationPreviewDialog(String animationName) {
        Bundle args = new Bundle();
        args.putString(DbConstants.EVENTS_ANIMATION_NAME, animationName);
        FragmentManager fm = getFragmentManager();
        AnimationPreviewDialog dialog = new AnimationPreviewDialog();
        dialog.setArguments(args);
        dialog.show(fm, "AnimationPreviewDialog");
    }

    private ArrayList<String> generateTimeBeforeOptions() {
        ArrayList<String> beforeOptions = new ArrayList<>();
        beforeOptions.add(context.getString(R.string.on_time));
        beforeOptions.add(context.getString(R.string.before_1_minute));
        beforeOptions.add(context.getString(R.string.before_2_minutes));
        beforeOptions.add(context.getString(R.string.before_5_minutes));
        beforeOptions.add(context.getString(R.string.before_10_minutes));
        beforeOptions.add(context.getString(R.string.before_15_minutes));
        beforeOptions.add(context.getString(R.string.before_30_minutes));
        beforeOptions.add(context.getString(R.string.before_60_minutes));
        beforeOptions.add(context.getString(R.string.before_120_minutes));
        return beforeOptions;
    }

    private ArrayList<String> generateAnimationOptions() {
        ArrayList<String> animtionOptions = new ArrayList<>();
        animtionOptions.add("bart_and_homer");
        animtionOptions.add("bird");
        animtionOptions.add("brain");
        animtionOptions.add("brat_and_lisa");
        animtionOptions.add("coyote");
        animtionOptions.add("eevee");
        animtionOptions.add("going_my_way");
        animtionOptions.add("itchy_and_scratchy");
        animtionOptions.add("maggie");
        animtionOptions.add("oddie");
        animtionOptions.add("pegasus");
        animtionOptions.add("peon");
        animtionOptions.add("peter_pan");
        animtionOptions.add("pikachu_plays");
        animtionOptions.add("pikachu_walks");
        animtionOptions.add("road_runner");
        animtionOptions.add("rooster");
        animtionOptions.add("smurf_jumps");
        animtionOptions.add("sonic");
        animtionOptions.add("spiderman");
        animtionOptions.add("super_hero");
        animtionOptions.add("tiger_jumping");
        animtionOptions.add("tinker_bell");
        animtionOptions.add("winnie_the_pooh");
        return animtionOptions;
    }

    private ArrayList<String> generateRepeatOptions() {
        Calendar calendar = Calendar.getInstance(); // TODO Replace with calendar that creating the start date
        String[] dayOfWeek = getResources().getStringArray(R.array.days_of_week);
        String[] monthPfYear = getResources().getStringArray(R.array.month_of_year);
        ArrayList<String> options = new ArrayList<>(5);
        options.add("One time Event");
        options.add("EveryDay - till i will terminate it");
        options.add("Weekly - every " + dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        options.add("Monthly - every " + calendar.get(Calendar.DAY_OF_MONTH) + " on month");
        options.add("Yearly - every " + calendar.get(Calendar.DAY_OF_MONTH) + " on " + monthPfYear[calendar.get(Calendar.MONTH)]);
        return options;
    }

    private void openTimePickerDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.show(fragmentManager, "timePickerDialog");
    }

    @Override
    public void onTimePicked(int hours, int minutes) {
        // TODO Store this values
        this.hour = hours;
        this.minute = minutes;
        String hoursStr = String.valueOf(hours);
        String minutesStr = String.valueOf(minutes);
        if (10 > hours) {
            hoursStr = "0" + hoursStr;
        }
        if (10 > minutes) {
            minutesStr = "0" + minutesStr;
        }
        timeTextView.setText(hoursStr + ":" + minutesStr);
    }

    private void openDatePickerDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog.show(fragmentManager, "DatePickerDialog");
    }

    @Override
    public void onDatePicked(Date date) {
        dateTextView.setText(TimeUtils.getDateStr(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        int receiveBootComplete = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_BOOT_COMPLETED);
        int readExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        boolean overLay = Settings.canDrawOverlays(this);

        return receiveBootComplete == PackageManager.PERMISSION_GRANTED &&
                readExternalStorage == PackageManager.PERMISSION_GRANTED &&
                overLay;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECEIVE_BOOT_COMPLETED, READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean bootRes = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readXstorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (bootRes && readXstorage) {
                        if (Settings.canDrawOverlays(context)) {
                            //Toast.makeText(context, "Permission Granted, Now you can use the application.", Toast.LENGTH_LONG).show();
                            continueAppLoading();
                        } else {
                            //Toast.makeText(context, "Permission Denied, You cannot access the application.", Toast.LENGTH_LONG).show();
                            permissionToDrawOverlays();
                        }
                    } else {
                        Toast.makeText(context, "Permission Denied, You cannot access the application.", Toast.LENGTH_LONG).show();
                        if (!hasPermissions(context, RECEIVE_BOOT_COMPLETED)) {
                            Toast.makeText(context, "You need to allow access to all the permissions", Toast.LENGTH_LONG).show();
                            requestPermission();
                        }
                    }
                }
        }
    }

    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        } else {
            continueAppLoading();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PERM_REQUEST_CODE_DRAW_OVERLAYS == requestCode) {  // ResultsCode is 0 in any case.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    permissionToDrawOverlays();
                } else {
                    continueAppLoading();
                }
            } else {
                continueAppLoading();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (int i = 0; i < permissions.length; i++) {
                if (ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private class SpinnerAdapter extends ArrayAdapter<String> {

        private int view;
        private LayoutInflater inflater;
        private Activity activity;
        private ArrayList<String> data;

        public SpinnerAdapter(Activity activity, int textViewResourceId, ArrayList<String> strings) {
            super(activity, textViewResourceId, strings);

            this.activity = activity;
            data = strings;
            view = textViewResourceId;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            // Inflate spinner_adapter_opt_activity.xml file for each row
            View row = inflater.inflate(view, parent, false);

            TextView spinnerAdapterOptActivityTextView = (TextView) row.findViewById(R.id.spinnerAdapterOptActivityTextView);

            spinnerAdapterOptActivityTextView.setText(data.get(position));

            return row;
        }
    }

}
