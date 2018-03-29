package com.em_projects.reminder.storage.db;

/**
 * * Created by eyal muchtar on 12/19/17.
 */

public class DbConstants {
    public static final String DB_NAME = "remainder.db";
    public static final int DATABASE_VERSION = 2;

    // Events db properties
    public static final String EVENTS_TABLE_NAME = "eventsTb";                      // Table Name

    public static final String EVENTS_ID = "events_id";                             // INTEGER
    public static final String EVENTS_SUBJECT = "events_subject";                   // TEXT
    public static final String EVENTS_START_DATE = "events_start_date";             // INTEGER
    public static final String EVENT_ALARM_SECONDS_BEFORE = "event_alarm_seconds_before";       // INTEGER
    public static final String EVENTS_DURATION = "events_duration";                 // INTEGER
    public static final String EVENTS_REPEAT_TYPE= "events_repeat_type";            // INTEGER
    public static final String EVENTS_ANIMATION_NAME = "events_animation_name";     // TEXT
//    public static final String EVENTS_NUMBER_OF_ALERTS = "events_number_of_alerts"; // INTEGER
    public static final String EVENTS_ALERTS_INTERVAL = "events_alerts_interval";   // INTEGER
    public static final String EVENTS_TUNE_NAME = "events_tune_name";               // TEXT

    // Create Events DB
    public static final String CREATE_EVENTS_TABLE =
            "CREATE TABLE " + EVENTS_TABLE_NAME +
                    "("
                    + EVENTS_ID + " INTEGER PRIMARY KEY autoincrement,"
                    + EVENTS_SUBJECT + " TEXT,"
                    + EVENTS_START_DATE + " INTEGER,"
                    + EVENTS_DURATION + " INTEGER,"
                    + EVENT_ALARM_SECONDS_BEFORE + " INTEGER,"
                    + EVENTS_REPEAT_TYPE + " INTEGER,"
                    + EVENTS_ANIMATION_NAME + " TEXT,"
//                    + EVENTS_NUMBER_OF_ALERTS + " INTEGER,"
                    + EVENTS_ALERTS_INTERVAL + " INTEGER,"
                    + EVENTS_TUNE_NAME + " TEXT"
                    + ")";

    // Drop Events DB
    public static final String DROP_EVENTS_TABLE =
            "DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME;
}
