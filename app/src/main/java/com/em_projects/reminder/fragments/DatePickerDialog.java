package com.em_projects.reminder.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;

import com.em_projects.reminder.R;
import com.em_projects.reminder.ui.custom_text.CustomButton;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by eyalmuchtar on 12/23/17.
 */

// Ref: https://stackoverflow.com/questions/9219367/android-datepicker-typeface

public class DatePickerDialog extends DialogFragment {

    private DatePicker simpleDatePicker;
    private CustomButton saveButton;
    private CustomButton cancelButton;


    public interface OnDatePickedListener {
        void onDatePicked(Date date);
    }
    private OnDatePickedListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnDatePickedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_date_picker, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "caveat_regular.ttf");

        simpleDatePicker = view.findViewById(R.id.simpleDatePicker);
        simpleDatePicker.setSpinnersShown(false);
        initDatePicker();
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onDatePicked(datePickerToDate());
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

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        simpleDatePicker.updateDate(year, month, day);
    }

    private Date datePickerToDate() {
            int day = simpleDatePicker.getDayOfMonth();
            int month = simpleDatePicker.getMonth();
            int year =  simpleDatePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            return calendar.getTime();
    }
}
