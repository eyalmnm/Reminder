package com.em_projects.reminder.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.em_projects.reminder.model.Event;

import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 12/19/17.
 */

public class EventsDbHandler {
    private static final String TAG = "EventsDbHandler";

    private EventsDbHelper dbHelper;

    private static EventsDbHandler instance;
    public static EventsDbHandler getInstance(Context context) {
        if (null == instance) {
            instance = new EventsDbHandler(context);
        }
        return instance;
    }

    /**
     * Constructor
     *
     * @param context calling activity context
     */
    private EventsDbHandler(Context context) {
        dbHelper = new EventsDbHelper(context);
    }


    public ArrayList<Event> getAll() {
        ArrayList<Event> events = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().query(DbConstants.EVENTS_TABLE_NAME, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_ID));
                String subject = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_SUBJECT));
                long startDate = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_START_DATE));
                long duration = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_DURATION));
                String repeatType = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_REPEAT_TYPE));
                String animationName = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_ANIMATION_NAME));
//                int numberOfAlerts = cursor.getInt(cursor.getColumnIndex(DbConstants.EVENTS_NUMBER_OF_ALERTS));
                long alertsInterval = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_ALERTS_INTERVAL));
                String tuneName = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_TUNE_NAME));
                Event event = new Event(String.valueOf(id), subject, startDate, duration,
                        Event.getRepeatType(repeatType), animationName, /*numberOfAlerts,*/ alertsInterval, tuneName);
                events.add(event);
            } while (cursor.moveToNext());
        }
        return events;
    }

    public ArrayList<Event> getAllByStartDate() {
        ArrayList<Event> events = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().query(DbConstants.EVENTS_TABLE_NAME, null,
                null, null, null, null, DbConstants.EVENTS_START_DATE +" DESC");
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_ID));
                String subject = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_SUBJECT));
                long startDate = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_START_DATE));
                long duration = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_DURATION));
                String repeatType = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_REPEAT_TYPE));
                String animationName = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_ANIMATION_NAME));
//                int numberOfAlerts = cursor.getInt(cursor.getColumnIndex(DbConstants.EVENTS_NUMBER_OF_ALERTS));
                long alertsInterval = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_ALERTS_INTERVAL));
                String tuneName = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_TUNE_NAME));
                Event event = new Event(String.valueOf(id), subject, startDate, duration,
                        Event.getRepeatType(repeatType), animationName, /*numberOfAlerts,*/ alertsInterval, tuneName);
                events.add(event);
            } while (cursor.moveToNext());
        }
        return events;
    }

    public Event getEvent(String id) {
        Cursor cursor = dbHelper.getReadableDatabase().query(DbConstants.EVENTS_TABLE_NAME, null,
                DbConstants.EVENTS_ID + "=?", new String[]{id}, null, null, null);
        if (true ==cursor.moveToFirst()) {
            String subject = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_SUBJECT));
            long startDate = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_START_DATE));
            long duration = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_DURATION));
            String repeatType = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_REPEAT_TYPE));
            String animationName = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_ANIMATION_NAME));
//            int numberOfAlerts = cursor.getInt(cursor.getColumnIndex(DbConstants.EVENTS_NUMBER_OF_ALERTS));
            long alertsInterval = cursor.getLong(cursor.getColumnIndex(DbConstants.EVENTS_ALERTS_INTERVAL));
            String tuneName = cursor.getString(cursor.getColumnIndex(DbConstants.EVENTS_TUNE_NAME));
            return new Event(id, subject, startDate, duration,
                    Event.getRepeatType(repeatType), animationName, /*numberOfAlerts,*/ alertsInterval, tuneName);
        } else {
            return null;
        }
    }

    public void addEvent(Event event) throws SQLException {
        if (null != event) {
            SQLiteDatabase sql = dbHelper.getWritableDatabase();
            if (null != sql) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbConstants.EVENTS_ID, event.getId());
                contentValues.put(DbConstants.EVENTS_SUBJECT, event.getSubject());
                contentValues.put(DbConstants.EVENTS_START_DATE, event.getStartDate());
                contentValues.put(DbConstants.EVENTS_DURATION, event.getDuration());
                contentValues.put(DbConstants.EVENTS_REPEAT_TYPE, event.getRepeatType());
                contentValues.put(DbConstants.EVENTS_ANIMATION_NAME, event.getAnimationName());
//                contentValues.put(DbConstants.EVENTS_NUMBER_OF_ALERTS, event.getNumberOfAlerts());
                contentValues.put(DbConstants.EVENTS_ALERTS_INTERVAL, event.getAlertsInterval());
                contentValues.put(DbConstants.EVENTS_TUNE_NAME, event.getTuneName());
                try {
                    sql.insertOrThrow(DbConstants.EVENTS_TABLE_NAME, null, contentValues);
                } catch (SQLException e) {
                    throw new SQLException(e.getMessage());
                } finally {
                    sql.close();
                }
            }
        }
    }

    public void updateEvent(Event event) throws SQLException {
        if (null != event) {
            SQLiteDatabase sql = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConstants.EVENTS_SUBJECT, event.getSubject());
            contentValues.put(DbConstants.EVENTS_START_DATE, event.getStartDate());
            contentValues.put(DbConstants.EVENTS_DURATION, event.getDuration());
            contentValues.put(DbConstants.EVENTS_REPEAT_TYPE, event.getRepeatType());
            contentValues.put(DbConstants.EVENTS_ANIMATION_NAME, event.getAnimationName());
//            contentValues.put(DbConstants.EVENTS_NUMBER_OF_ALERTS, event.getNumberOfAlerts());
            contentValues.put(DbConstants.EVENTS_ALERTS_INTERVAL, event.getAlertsInterval());
            contentValues.put(DbConstants.EVENTS_TUNE_NAME, event.getTuneName());
            try {
                sql.update(DbConstants.EVENTS_TABLE_NAME, contentValues, DbConstants.EVENTS_ID + "=?", new String[]{event.getId()});
            } catch (SQLException e) {
                throw new SQLException(e.getMessage());
            } finally {
                sql.close();
            }
        }
    }

    public void deleteEvent(Event event) throws SQLException {
        if (null != event) {
            SQLiteDatabase sql = dbHelper.getWritableDatabase();

            try {
                sql.delete(DbConstants.EVENTS_TABLE_NAME, DbConstants.EVENTS_ID + "=?", new String[]{String.valueOf(event.getId())});
            } catch (SQLException e) {
                throw new SQLException(e.getMessage());
            } finally {
                sql.close();
            }
        }
    }

    public void close() {
        if (null != dbHelper) {
            dbHelper.close();
        }
        dbHelper = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
