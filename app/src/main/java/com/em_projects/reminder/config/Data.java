package com.em_projects.reminder.config;

import android.content.Context;

import com.em_projects.reminder.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by eyalmuchtar on 3/26/18.
 */

public class Data {

    private Context context;

    public static Data getInstance(Context context) {
        if (null == instance) {
            instance = new Data(context);
        }
        return instance;
    }
    private static Data instance = null;

    private Data(Context context) {
        this.context = context;
    }

    public ArrayList<String> generateTimeBeforeOptions() {
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

    public ArrayList<String> generateAnimationOptions() {
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

    public ArrayList<String> generateRepeatOptions() {
        Calendar calendar = Calendar.getInstance(); // TODO Replace with calendar that creating the start date
        String[] dayOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        String[] monthPfYear = context.getResources().getStringArray(R.array.month_of_year);
        ArrayList<String> options = new ArrayList<>(5);
        options.add("One time Event");
        options.add("EveryDay - till i will terminate it");
        options.add("Weekly - every " + dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        options.add("Monthly - every " + calendar.get(Calendar.DAY_OF_MONTH) + " on month");
        options.add("Yearly - every " + calendar.get(Calendar.DAY_OF_MONTH) + " on " + monthPfYear[calendar.get(Calendar.MONTH)]);
        return options;
    }

}
