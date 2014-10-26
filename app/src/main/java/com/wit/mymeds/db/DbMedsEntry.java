package com.wit.mymeds.db;

import android.provider.BaseColumns;

/**
 * Created by Diogo on 26-10-2014.
 */
public final class DbMedsEntry implements BaseColumns {
        public static final String TABLE_NAME = "meds";
        public static final String COLUMN_NAME_MED_NAME = "med_name";
        public static final String COLUMN_NAME_MED_SUNDAY = "med_sunday";
        public static final String COLUMN_NAME_MED_MONDAY = "med_monday";
        public static final String COLUMN_NAME_MED_TUESDAY = "med_tuesday";
        public static final String COLUMN_NAME_MED_WEDNESDAY = "med_wednesday";
        public static final String COLUMN_NAME_MED_THURSDAY = "med_thursday";
        public static final String COLUMN_NAME_MED_FRIDAY = "med_friday";
        public static final String COLUMN_NAME_MED_SATURDAY = "med_saturday";
        public static final String COLUMN_NAME_MED_START_HOUR = "med_start_hour";
        public static final String COLUMN_NAME_MED_FREQUENCY_HOUR = "med_frequency_hour";
    }

