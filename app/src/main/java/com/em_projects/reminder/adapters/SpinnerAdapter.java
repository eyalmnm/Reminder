package com.em_projects.reminder.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.em_projects.reminder.R;

import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 3/14/18.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {

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
