package com.em_projects.reminder.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

/**
 * * Created by eyalmuchtar on 12/19/17.
 */

public class EventsDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "EventsDbHelper";

    // Constructor
    public EventsDbHelper(Context context) {
        super(context, DbConstants.DB_NAME,
                null, DbConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        try {
            db.execSQL(DbConstants.CREATE_EVENTS_TABLE);
        } catch (SQLiteException ex) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "onCreate");
            FirebaseCrash.report(ex);
            Log.e(TAG, "Create table exception: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL(DbConstants.DROP_EVENTS_TABLE);
        onCreate(db);
    }
}
