package com.em_projects.reminder.alerts_data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.em_projects.reminder.R;
import com.em_projects.reminder.model.Event;
import com.em_projects.reminder.storage.db.EventsDbHandler;
import com.em_projects.reminder.ui.custom_text.CustomButton;
import com.em_projects.reminder.ui.custom_text.CustomTextView;
import com.em_projects.reminder.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 1/16/18.
 */

public class AlertsListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlertsListActivity";

    private CustomTextView alertsListTitleTextView;
    private CustomTextView alertsListSubTitleTextView;
    private ImageView alertsListListEmptyImage;
    private ListView alertsListListView;
    private CustomButton closeButton;
    private FloatingActionButton addEventFAB;

    private ArrayList<Event> events;
    private EventsListAdapter adapter;

    private Context context;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alerts_list);
        Log.d(TAG, "onCreate");
        context = this;

        alertsListTitleTextView = findViewById(R.id.alertsListTitleTextView);
        alertsListSubTitleTextView = findViewById(R.id.alertsListSubTitleTextView);
        alertsListListEmptyImage = findViewById(R.id.alertsListListEmptyImage);
        alertsListListView = findViewById(R.id.alertsListListView);
        closeButton = findViewById(R.id.closeButton);
        addEventFAB = findViewById(R.id.addEventFAB);

        initAlertsList();

        initButtons();

        switchViews();
    }

    private void initAlertsList() {
        events = new ArrayList<>();
        events = EventsDbHandler.getInstance(this).getAll();
        adapter = new EventsListAdapter();
        alertsListListView.setAdapter(adapter);
        switchViews();
    }

    private void switchViews() {
        if (0 == events.size()) {
            alertsListListEmptyImage.setVisibility(View.VISIBLE);
            alertsListListView.setVisibility(View.INVISIBLE);
            alertsListSubTitleTextView.setVisibility(View.INVISIBLE);
        } else {
            alertsListListEmptyImage.setVisibility(View.INVISIBLE);
            alertsListListView.setVisibility(View.VISIBLE);
            alertsListSubTitleTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initButtons() {
        closeButton.setOnClickListener(this);
        addEventFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                onBackPressed();
                break;
            case R.id.addEventFAB:
                addNewEvent();
                finish();
                break;
        }
    }

    private void addNewEvent() {
        // TODO do something here
    }

    private class EventsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            if (null == view) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.layout_alarm_item, null);
                holder.subjectTextView = view.findViewById(R.id.subjectTextView);
                holder.dateTextView = view.findViewById(R.id.dateTextView);
                holder.timeTextView = view.findViewById(R.id.timeTextView);
                holder.repeateTextView = view.findViewById(R.id.repeateTextView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Event event = events.get(position);
            holder.subjectTextView.setText(String.valueOf(event.getSubject()));
            holder.dateTextView.setText(TimeUtils.getDateStr(event.getStartDate()));
            holder.timeTextView.setText(TimeUtils.getTimeStr(event.getStartDate()));
            holder.repeateTextView.setText(event.getRepeatType());
            return view;
        }
    }

    private class ViewHolder {
        CustomTextView subjectTextView;
        CustomTextView dateTextView;
        CustomTextView timeTextView;
        CustomTextView repeateTextView;
    }
}
