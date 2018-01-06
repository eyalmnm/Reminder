package com.em_projects.reminder.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TimePicker;

import com.em_projects.reminder.R;
import com.em_projects.reminder.ui.custom_text.CustomButton;

import java.util.Calendar;

/**
 * Created by eyalmuchtar on 12/24/17.
 */

public class TimePickerDialog extends DialogFragment {

    public static final long MINUTE = 60 * 1000;
    public static final long HOUR = 60 * MINUTE;

    private TimePicker simpleTimePicker;
    private CustomButton saveButton;
    private CustomButton cancelButton;

    public interface OnTimePickedListener {
        void onTimePicked(int hours, int minutes);
    }
    private OnTimePickedListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnTimePickedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_time_picker, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "caveat_regular.ttf");

        simpleTimePicker = view.findViewById(R.id.simpleTimePicker);
        initDatePicker();
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onTimePicked(simpleTimePicker.getCurrentHour(), simpleTimePicker.getCurrentMinute());
                }
                dismiss();
            }
        });

        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initDatePicker() {
        Calendar cal = Calendar.getInstance();

        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        simpleTimePicker.setCurrentHour(hours);
        simpleTimePicker.setCurrentMinute(minutes);
    }

    private long timePickerToMilis() {
        int hours = simpleTimePicker.getCurrentHour();
        int minutes = simpleTimePicker.getCurrentMinute();
        return (hours * HOUR) + (minutes * MINUTE);
    }

}
