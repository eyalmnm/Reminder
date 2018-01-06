package com.em_projects.reminder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * Format the given millis to date and time string
     *
     * @param currentTime the time to be formatted
     * @return date and time string
     */
    public static String timeToFormatedString(long currentTime) {
        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date(currentTime));
        return out;
    }

    public static String getDateStr(long millis) {
        String out = new SimpleDateFormat("dd-MM-yyyy").format(new Date(millis));
        return out;
    }

    public static String getTimeStr(long millis) {
        String out = new SimpleDateFormat("HH:mm").format(new Date(millis));
        return out;
    }

    public static String getDateStr(Date date) {
        String out = new SimpleDateFormat("dd-MM-yyyy").format(date);
        return out;
    }

    public static String getTimeStr(Date date) {
        String out = new SimpleDateFormat("HH:mm").format(date);
        return out;
    }

    public static String imageFormatedTime(long currentTime) {
        String out = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date(currentTime));
        return out;
    }

}
