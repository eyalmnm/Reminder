package com.em_projects.reminder.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.em_projects.reminder.MainActivity;
import com.em_projects.reminder.R;
import com.em_projects.reminder.adapters.SpinnerAdapter;
import com.em_projects.reminder.config.Data;
import com.em_projects.reminder.fragments.DatePickerDialog;
import com.em_projects.reminder.fragments.TimePickerDialog;
import com.em_projects.reminder.model.Event;
import com.em_projects.reminder.storage.db.EventsDbHandler;
import com.em_projects.reminder.ui.custom_text.CustomButton;
import com.em_projects.reminder.ui.custom_text.CustomCheckBox;
import com.em_projects.reminder.ui.custom_text.CustomEditText;
import com.em_projects.reminder.ui.custom_text.CustomRadioButton;
import com.em_projects.reminder.ui.custom_text.CustomTextView;
import com.em_projects.reminder.ui.widgets.ringtonepicker.RingtonePickerDialog;
import com.em_projects.reminder.ui.widgets.ringtonepicker.RingtonePickerListener;
import com.em_projects.reminder.utils.StringUtils;
import com.em_projects.reminder.utils.TimeUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by eyalmuchtar on 3/12/18.
 */

public class EditEventDialog extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDatePickedListener,
        TimePickerDialog.OnTimePickedListener {

    private static final String TAG = "EditEventDialog";

    // UI Components
    private CustomEditText subjectEditText;

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

    private boolean wholeDay = false;
    private long duration = 0;
    private long durationInHours = 0;

    private Spinner eventRepeatSpinner;
    private SpinnerAdapter eventRepeatSpinnerAdapter;
    private Event.RepeatType repeatType = Event.RepeatType.NONE;

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

    private Event event;


    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_event);
        Log.d(TAG, "onCreate");
        context = this;

        Intent intent = getIntent();
        if (null != intent) {
            event = (Event) intent.getSerializableExtra("event");
        }

        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // Loading the view Components
        subjectEditText = findViewById(R.id.subjectEditText);

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
        alertsInterval = 5 * MainActivity.MINUTE_MILLIS;

        addSoundIndicator = findViewById(R.id.addSoundIndicator);
        soundSelectionTextView = findViewById(R.id.soundSelectionTextView);
        soundSelectionTextView.setVisibility(View.INVISIBLE);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        continueAppLoading();
    }

    private void continueAppLoading() {
        initializeView();
    }

    private void initializeView() {
        subjectEditText.setText(null == event ? "" : event.getSubject());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(null == event ? System.currentTimeMillis() : event.getStartDate());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        dateTextView.setText(TimeUtils.getDateStr(calendar.getTimeInMillis()));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
        timeTextView.setText(TimeUtils.getTimeStr(calendar.getTimeInMillis()));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });

        Resources resource = context.getResources();
        ArrayList<String> durationOptions = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.duration_options)));
        eventDurationSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, durationOptions);
        eventDurationSpinner.setAdapter(eventDurationSpinnerAdapter);
        eventDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeTextView.setEnabled(true);
                wholeDay = false;
                switch (position) {
                    case MainActivity.MINUTES_15:
                        duration = 15 * MainActivity.MINUTE_MILLIS;
                        break;
                    case MainActivity.MINUTES_30:
                        duration = 30 * MainActivity.MINUTE_MILLIS;
                        break;
                    case MainActivity.MINUTES_60:
                        duration = 1 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.MINUTES_120:
                        duration = 2 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.WHOLE_DAY:
                        duration = 24 * MainActivity.HOUR_MILLIS;
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
                    case MainActivity.HOURS_2_5:
                        durationInHours = (long) (2.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_3_0:
                        durationInHours = 3 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_3_5:
                        durationInHours = (long) (3.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_4_0:
                        durationInHours = 4 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_4_5:
                        durationInHours = (long) (4.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_5_0:
                        durationInHours = 5 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_5_5:
                        durationInHours = (long) (5.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_6_0:
                        durationInHours = 6 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_6_5:
                        durationInHours = (long) (6.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_7_0:
                        durationInHours = 7 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_7_5:
                        durationInHours = (long) (7.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_8_0:
                        durationInHours = 8 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_8_5:
                        durationInHours = (long) (8.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_9_0:
                        durationInHours = 9 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_9_5:
                        durationInHours = (long) (9.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_10_0:
                        durationInHours = 10 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_10_5:
                        durationInHours = (long) (10.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_11_0:
                        durationInHours = 11 * MainActivity.HOUR_MILLIS;
                        break;
                    case MainActivity.HOURS_11_5:
                        durationInHours = (long) (11.5 * MainActivity.HOUR_MILLIS);
                        break;
                    case MainActivity.HOURS_12_0:
                        durationInHours = 12 * MainActivity.HOUR_MILLIS;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });
        int minutes = getMinutes(event.getDuration());
        if (1440 == minutes) {
            eventDurationSpinner.setSelection(4);
        } else if (15 == minutes) {
            eventDurationSpinner.setSelection(0);
        } else if (30 == minutes) {
            eventDurationSpinner.setSelection(1);
        } else if (60 == minutes) {
            eventDurationSpinner.setSelection(2);
        } else if (120 == minutes) {
            eventDurationSpinner.setSelection(3);
        } else if (150 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(0);
        } else if (180 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(1);
        } else if (210 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(2);
        } else if (240 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(3);
        } else if (270 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(4);
        } else if (300 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(5);
        } else if (330 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(6);
        } else if (360 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(7);
        } else if (390 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(8);
        } else if (420 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(9);
        } else if (450 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(10);
        } else if (480 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(11);
        } else if (510 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(12);
        } else if (540 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(13);
        } else if (570 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(14);
        } else if (600 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(15);
        } else if (630 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(16);
        } else if (660 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(17);
        } else if (690 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(18);
        } else if (720 == minutes) {
            durationInHoursIndicator.setChecked(true);
            eventHoursDurationSpinner.setVisibility(View.VISIBLE);
            eventHoursDurationSpinner.setSelection(19);
        }

        ArrayList<String> repeatOptins = Data.getInstance(context).generateRepeatOptions();
        eventRepeatSpinnerAdapter = new SpinnerAdapter(this, R.layout.layout_spinner_view, repeatOptins);
        eventRepeatSpinner.setAdapter(eventRepeatSpinnerAdapter);
        eventRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case MainActivity.REPEAT_ONE_TIME:
                        repeatType = Event.RepeatType.NONE;
                        break;
                    case MainActivity.REPEAT_DAILY:
                        repeatType = Event.RepeatType.DAILY;
                        break;
                    case MainActivity.REPEAT_WEEKLY:
                        repeatType = Event.RepeatType.WEEKLY;
                        break;
                    case MainActivity.REPEAT_MONTHLY:
                        repeatType = Event.RepeatType.MONTHLY;
                        break;
                    case MainActivity.REPEAT_YEARLY:
                        repeatType = Event.RepeatType.YEARLY;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Leave empty
            }
        });

        String repeatTypeStr = event.getRepeatType();
        if (false == StringUtils.isNullOrEmpty(repeatTypeStr)) {
            if (true == "NONE".equalsIgnoreCase(repeatTypeStr)) {
                eventRepeatSpinner.setSelection(0);
            } else if (true == "DAILY".equalsIgnoreCase(repeatTypeStr)) {
                eventRepeatSpinner.setSelection(1);
            } else if (true == "WEEKLY".equalsIgnoreCase(repeatTypeStr)) {
                eventRepeatSpinner.setSelection(2);
            } else if (true == "MONTHLY".equalsIgnoreCase(repeatTypeStr)) {
                eventRepeatSpinner.setSelection(3);
            } else if (true == "YEARLY".equalsIgnoreCase(repeatTypeStr)) {
                eventRepeatSpinner.setSelection(4);
            }
        }

        ArrayList<String> timeBeforeOptions = Data.getInstance(context).generateTimeBeforeOptions();
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

        switch ((int) event.getTimeBeforeSec()) {
            case 60:
                eventTimeBeforeSpinner.setSelection(1);
                break;
            case 120:
                eventTimeBeforeSpinner.setSelection(2);
                break;
            case 300:
                eventTimeBeforeSpinner.setSelection(4);
                break;
            case 600:
                eventTimeBeforeSpinner.setSelection(5);
                break;
            case 900:
                eventTimeBeforeSpinner.setSelection(6);
                break;
            case 1800:
                eventTimeBeforeSpinner.setSelection(7);
                break;
            case 3600:
                eventTimeBeforeSpinner.setSelection(8);
                break;
            case 7200:
                eventTimeBeforeSpinner.setSelection(9);
                break;
            default:
                eventTimeBeforeSpinner.setSelection(0);
                break;
        }

        final ArrayList<String> animationOptions = Data.getInstance(context).generateAnimationOptions();
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
        int position = animationOptions.indexOf(event.getAnimationName());
        animationSelectionSpinner.setSelection(position);

        tuneName = event.getTuneName();
        if (true == StringUtils.isNullOrEmpty(tuneName)) {
            addSoundIndicator.setChecked(false);
            soundSelectionTextView.setVisibility(View.INVISIBLE);
        } else {
            addSoundIndicator.setChecked(true);
            soundSelectionTextView.setVisibility(View.VISIBLE);
            soundSelectionTextView.setText(getFileNameFromUri(tuneName));
            soundSelectionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRingTonePicker();
                }
            });
        }
    }

    private String getFileNameFromUri(String tuneUriStr) {
        String fileName = "default_file_name";
        Cursor returnCursor =
                getContentResolver().query(Uri.parse(tuneUriStr), null, null, null, null);
        try {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
            Log.d(TAG, "file name : " + fileName);
            return fileName;
        }catch (Exception e){
            Log.e(TAG, "getFileNameFromUri", e);
            //handle the failure cases here
        } finally {
            returnCursor.close();
        }
        return null;
    }

    private int getMinutes(long time) {
        return (int) (time / MainActivity.MINUTE_MILLIS);
    }

    private void openTimePickerDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putLong("data", event.getStartDate());
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.setArguments(args);
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
        Bundle args = new Bundle();
        args.putLong("data", event.getStartDate());
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog.setArguments(args);
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

    private void showRingTonePicker() {
        RingtonePickerDialog.Builder ringtonePickerBuilder =
                new RingtonePickerDialog.Builder(EditEventDialog.this, getSupportFragmentManager());

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
        if (false == StringUtils.isNullOrEmpty(tuneName)) {
            ringtonePickerBuilder.setCurrentRingtoneUri(Uri.parse(tuneName));
        }

        //Display the dialog.
        ringtonePickerBuilder.show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.okButton:
                Event editedEvent = createEvent();
                if (null == editedEvent) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("event", editedEvent);
                intent.putExtra("org_event", event);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.cancelButton:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private Event createEvent() {
        String eventId = this.event.getId();
        String subject = subjectEditText.getText().toString();
        if (true == StringUtils.isNullOrEmpty(subject)) {
            Toast.makeText(context, R.string.empty_subject, Toast.LENGTH_LONG).show();
            return null;
        }
        long eventDuration = duration;
        if (0 < durationInHours && durationInHoursIndicator.isChecked()) {
            eventDuration = durationInHours;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        if (wholeDay) {
            calendar.set(year, month, day, 0, 0);
        } else {
            calendar.set(year, month, day, hour, minute);
//                    if (0 < eventTimeBeforeSec) {
//                        calendar.add(Calendar.SECOND, (int)(-1 * eventTimeBeforeSec));
//                    }
        }
        long now = System.currentTimeMillis();
        long startDate = calendar.getTimeInMillis();
        if (0 > ((startDate - (eventTimeBeforeSec * 1000)) - now)) {
            Toast.makeText(context, R.string.event_starts_in_past, Toast.LENGTH_LONG).show();
            return null;
        }
        String calendarTime = TimeUtils.timeToFormatedString(calendar.getTimeInMillis());
        Log.d(TAG, "Time in calendar is: " + calendarTime);
        int radioButtonId = alertRepeatOptionsRadioGroup.getCheckedRadioButtonId();
        if (R.id.everyThreeMinuteRadioButton == radioButtonId) {
            alertsInterval = 3 * MainActivity.MINUTE_MILLIS;
        } else if (R.id.everyFiveMinuteRadioButton == radioButtonId) {
            alertsInterval = 5 * MainActivity.MINUTE_MILLIS;
        } else {
            alertsInterval = 1 * MainActivity.MINUTE_MILLIS;
        }

        Event event = new Event(eventId, subject, startDate, eventDuration, eventTimeBeforeSec,
                repeatType, animationName, alertsInterval, tuneName);
        return event;
    }
}
