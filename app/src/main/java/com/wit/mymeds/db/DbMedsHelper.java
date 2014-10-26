package com.wit.mymeds.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Diogo on 26-10-2014.
 */
public class DbMedsHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyMeds.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE= " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + DbMedsEntry.TABLE_NAME + " (" +
            DbMedsEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_NAME + TEXT_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_SUNDAY + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_MONDAY + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_TUESDAY + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_THURSDAY + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_FRIDAY + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_SATURDAY + INTEGER_TYPE + COMMA_SEP +
            // store as Unix Time, the number of seconds since 1970-01-01 00:00:00 UTC
            DbMedsEntry.COLUMN_NAME_MED_START_HOUR + INTEGER_TYPE + COMMA_SEP +
            DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR + INTEGER_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbMedsEntry.TABLE_NAME;

    public DbMedsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
