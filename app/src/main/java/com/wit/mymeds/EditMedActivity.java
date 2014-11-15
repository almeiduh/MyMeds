package com.wit.mymeds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wit.mymeds.alarm.MyAlarmService;
import com.wit.mymeds.db.DbMedsEntry;
import com.wit.mymeds.db.DbMedsHelper;
import com.wit.mymeds.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class EditMedActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_med);

        Intent intent = getIntent();
        String oldMedName = intent.getExtras().getString("MEDNAME");

        DbMedsHelper mDbHelper = new DbMedsHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        List list = new ArrayList<Map<String, String>>();
        String[] projection = {
                DbMedsEntry._ID,
                DbMedsEntry.COLUMN_NAME_MED_NAME,
                DbMedsEntry.COLUMN_NAME_MED_SUNDAY,
                DbMedsEntry.COLUMN_NAME_MED_MONDAY,
                DbMedsEntry.COLUMN_NAME_MED_TUESDAY,
                DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY,
                DbMedsEntry.COLUMN_NAME_MED_THURSDAY,
                DbMedsEntry.COLUMN_NAME_MED_FRIDAY,
                DbMedsEntry.COLUMN_NAME_MED_SATURDAY,
                DbMedsEntry.COLUMN_NAME_MED_START_HOUR,
                DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR,
                DbMedsEntry.COLUMN_NAME_MED_ICON
        };

        String sortOrder =
                DbMedsEntry.COLUMN_NAME_MED_NAME + " COLLATE NOCASE"; // Don't care about case

        Cursor c = db.query(
                DbMedsEntry.TABLE_NAME,  // The table to query
                projection,              // The columns to return
                DbMedsEntry.COLUMN_NAME_MED_NAME + " = '" + oldMedName +"'", // The columns for the WHERE clause
                null,                    // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                sortOrder                     // The sort order
        );

        if (c.getCount() != 0) {
            c.moveToFirst();
            do {
                boolean isSunday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_SUNDAY)) == 1;
                boolean isMonday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_MONDAY)) == 1;
                boolean isTuesday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_TUESDAY)) == 1;
                boolean isWednesday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY)) == 1;
                boolean isThursday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_THURSDAY)) == 1;
                boolean isFriday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_FRIDAY)) == 1;
                boolean isSaturday = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_SATURDAY)) == 1;
                String startHour = c.getString(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_START_HOUR));
                int repeatTime = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR));
                int iconId = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_ICON));

                // Update views
                ((EditText)findViewById(R.id.form_name_edit)).setText(oldMedName);

                if(isSunday)
                    ((CheckBox)findViewById(R.id.sunCheckBox)).setChecked(true);
                if(isMonday)
                    ((CheckBox)findViewById(R.id.monCheckBox)).setChecked(true);
                if(isTuesday)
                    ((CheckBox)findViewById(R.id.tueCheckBox)).setChecked(true);
                if(isWednesday)
                    ((CheckBox)findViewById(R.id.wedCheckBox)).setChecked(true);
                if(isThursday)
                    ((CheckBox)findViewById(R.id.thuCheckBox)).setChecked(true);
                if(isFriday)
                    ((CheckBox)findViewById(R.id.friCheckBox)).setChecked(true);
                if(isSaturday)
                    ((CheckBox)findViewById(R.id.satCheckBox)).setChecked(true);

                ((TextView)findViewById(R.id.form_time_text)).setText(startHour);
                ((SeekBar)findViewById(R.id.seekBarRepeatHours)).setProgress(repeatTime-1);
                ((TextView)findViewById(R.id.form_repeat_time_value)).setText(repeatTime + " hours");


                switch(iconId) {
                     case R.integer.red_icon_id:
                         ((RadioButton)findViewById(R.id.radio_red_icon)).setChecked(true);
                         break;
                     case R.integer.blue_icon_id:
                         ((RadioButton)findViewById(R.id.radio_blue_icon)).setChecked(true);
                         break;
                     case R.integer.grey_icon_id:
                         ((RadioButton)findViewById(R.id.radio_grey_icon)).setChecked(true);
                         break;
                 }

            } while (c.moveToNext());

        }


        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarRepeatHours);
        final TextView seekBarValue = (TextView)findViewById(R.id.form_repeat_time_value);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(progress +
                        getResources().getInteger(R.integer.min_repeat_time) + " hours");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_med, menu);
        return true;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_med_button:
                if(areAllFormFieldsValid()) {
                    DbMedsHelper mDbHelper = new DbMedsHelper(getApplicationContext());
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();
                    Intent intent = getIntent();
                    String oldMedName = intent.getExtras().getString("MEDNAME");
                    if(deleteMedFromDb(oldMedName, db)) {
                        deleteMedAlarms(oldMedName);
                        insertMedIntoDb();
                    }
                }
                return true;
            default:
                return false;
        }
    }

    private boolean deleteMedFromDb(String oldMedName, SQLiteDatabase db) {
        return db.delete(DbMedsEntry.TABLE_NAME,
                DbMedsEntry.COLUMN_NAME_MED_NAME + "='" + oldMedName + "'", null) > 0;
    }

    private void deleteMedAlarms(String oldMedName) {
        Intent myIntent = new Intent(this, MyAlarmService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this,
                oldMedName.hashCode(),
                myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void insertMedIntoDb() {
        DbMedsHelper mDbHelper = new DbMedsHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Get form values
        String medName = ((EditText)findViewById(R.id.form_name_edit)).getText().toString();

        boolean isSunday = ((CheckBox)findViewById(R.id.sunCheckBox)).isChecked();
        boolean isMonday = ((CheckBox)findViewById(R.id.monCheckBox)).isChecked();
        boolean isTuesday = ((CheckBox)findViewById(R.id.tueCheckBox)).isChecked();
        boolean isWednesday = ((CheckBox)findViewById(R.id.wedCheckBox)).isChecked();
        boolean isThursday = ((CheckBox)findViewById(R.id.thuCheckBox)).isChecked();
        boolean isFriday = ((CheckBox)findViewById(R.id.friCheckBox)).isChecked();
        boolean isSaturday = ((CheckBox)findViewById(R.id.satCheckBox)).isChecked();

        String startHour = ((TextView)findViewById(R.id.form_time_text)).getText().toString();

        int repeatTime = ((SeekBar)findViewById(R.id.seekBarRepeatHours)).getProgress() +
                getResources().getInteger(R.integer.min_repeat_time);

        int iconId = R.integer.red_icon_id;
        RadioGroup radioGroup = ((RadioGroup) findViewById(R.id.radio_group_icon));
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        // Check which radio button was clicked
        switch(radioButtonId) {
            case R.id.radio_red_icon:
                iconId = R.integer.red_icon_id;
                break;
            case R.id.radio_blue_icon:
                iconId = R.integer.blue_icon_id;
                break;
            case R.id.radio_grey_icon:
                iconId = R.integer.grey_icon_id;
                break;
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbMedsEntry.COLUMN_NAME_MED_NAME, medName);
        values.put(DbMedsEntry.COLUMN_NAME_MED_SUNDAY, isSunday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_MONDAY, isMonday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_TUESDAY, isTuesday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY, isWednesday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_THURSDAY, isThursday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_FRIDAY, isFriday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_SATURDAY, isSaturday);
        values.put(DbMedsEntry.COLUMN_NAME_MED_START_HOUR, startHour);
        values.put(DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR, repeatTime);
        values.put(DbMedsEntry.COLUMN_NAME_MED_ICON, iconId);


        // Insert the new row, returning the primary key value of the new row
        try {
            long newRowId = db.insertOrThrow(DbMedsEntry.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "An entry with the same name already exists", Toast.LENGTH_LONG).show();
        }

        setupAlarmEasy(medName, startHour, repeatTime, iconId,
                isSunday, isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday);

        // close this activity
        setResult(Activity.RESULT_OK);
        this.finish();
    }

    private void setupAlarmEasy(String medName, String startHour, long repeatTime, long iconId,
                            boolean isSunday, boolean isMonday, boolean isTuesday,
                            boolean isWednesday, boolean isThursday, boolean isFriday,
                            boolean isSaturday) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM HH:mm");
            Date startDate = sdf.parse(startHour);

            Intent myIntent = new Intent(EditMedActivity.this, MyAlarmService.class);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_NAME, medName);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_ICON_ID, iconId);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_SUNDAY, isSunday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_MONDAY, isMonday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_TUESDAY, isTuesday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_WEDNESDAY, isWednesday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_THURSDAY, isThursday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_FRIDAY, isFriday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_SATURDAY, isSaturday);

            PendingIntent pendingIntent = PendingIntent.getService(EditMedActivity.this,
                    medName.hashCode(),
                    myIntent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();

            //calendar.set(Calendar.DAY_OF_WEEK, weekday);
            calendar.set(Calendar.HOUR_OF_DAY, startDate.getHours());
            calendar.set(Calendar.MINUTE, startDate.getMinutes());
            calendar.set(Calendar.SECOND, 0);

            Log.d(this.getLocalClassName(),"ALARM SET TO: " + calendar.getTime().toString());

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    repeatTime
                            * 60
                            * 60 * 1000, pendingIntent);

        } catch (ParseException e) {
            Log.e(this.getLocalClassName(), "ERROR PARSING DATE");
        }
    }

    private boolean areAllFormFieldsValid() {
        String medName = ((EditText)findViewById(R.id.form_name_edit)).getText().toString();
        String time = ((TextView) findViewById(R.id.form_time_text)).getText().toString();

        if(!isNameValid(medName)) {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!isTimeValid(time)){
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!isWeekDays()){
            Toast.makeText(this, "You must select at least one week day", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isWeekDays() {
        boolean isSunday = ((CheckBox)findViewById(R.id.sunCheckBox)).isChecked();
        boolean isMonday = ((CheckBox)findViewById(R.id.monCheckBox)).isChecked();
        boolean isTuesday = ((CheckBox)findViewById(R.id.tueCheckBox)).isChecked();
        boolean isWednesday = ((CheckBox)findViewById(R.id.wedCheckBox)).isChecked();
        boolean isThursday = ((CheckBox)findViewById(R.id.thuCheckBox)).isChecked();
        boolean isFriday = ((CheckBox)findViewById(R.id.friCheckBox)).isChecked();
        boolean isSaturday = ((CheckBox)findViewById(R.id.satCheckBox)).isChecked();

        return isSunday | isMonday | isTuesday | isWednesday | isThursday | isFriday | isSaturday;
    }

    private boolean isTimeValid(String time) {
        return time.trim().length() > 0;
    }

    private boolean isNameValid(String medName) {
        if(medName.trim().length() > 0)
            return true;

        return false;
    }

}
