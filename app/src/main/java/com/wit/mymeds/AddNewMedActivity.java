package com.wit.mymeds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wit.mymeds.alarm.MyAlarmService;
import com.wit.mymeds.db.DbMedsEntry;
import com.wit.mymeds.db.DbMedsHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddNewMedActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_med);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarRepeatHours);
        final TextView seekBarValue = (TextView)findViewById(R.id.form_repeat_time_value);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(progress + " hours");
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
                    insertMedIntoDb();
                }
                return true;
            default:
                return false;
        }
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

        int repeatTime = ((SeekBar)findViewById(R.id.seekBarRepeatHours)).getProgress();

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

            Intent myIntent = new Intent(AddNewMedActivity.this, MyAlarmService.class);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_NAME, medName);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_ICON_ID, iconId);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_SUNDAY, isSunday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_MONDAY, isMonday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_TUESDAY, isTuesday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_WEDNESDAY, isWednesday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_THURSDAY, isThursday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_FRIDAY, isFriday);
            myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_SATURDAY, isSaturday);

            PendingIntent pendingIntent = PendingIntent.getService(AddNewMedActivity.this,
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
                            //* 60
                            *  60 * 1000, pendingIntent);

        } catch (ParseException e) {
            Log.e(this.getLocalClassName(), "ERROR PARSING DATE");
        }
    }
/*
    private void setupAlarm(String medName, String startHour, long repeatTime, long iconId,
                            boolean isSunday, boolean isMonday, boolean isTuesday,
                            boolean isWednesday, boolean isThursday, boolean isFriday,
                            boolean isSaturday) {

        List<Integer> hoursList = new ArrayList<Integer>();

        String[] hourMin = startHour.split(":");

        int firstHour = Integer.valueOf(hourMin[0]);
        int min  = Integer.valueOf(hourMin[1]);

        if(repeatTime > 0) {
            while (firstHour >= 0) {
                firstHour -= repeatTime;
            }

            firstHour += repeatTime;

            while (firstHour < 24) {
                Log.d(this.getLocalClassName(), "Adding hour:"+ firstHour);
                hoursList.add(firstHour);
                firstHour += repeatTime;
            }
        } else {
            hoursList.add(firstHour);
        }

        for(int hour : hoursList) {
            setAlarm(medName, min, hour, iconId, isSunday, isMonday, isTuesday,
                    isWednesday, isThursday, isFriday, isSaturday);
        }
    }
*/
    /*
    private void setAlarm(String medName, int min, int hour, long iconId,
                                    boolean isSunday, boolean isMonday, boolean isTuesday,
                                    boolean isWednesday, boolean isThursday, boolean isFriday,
                                    boolean isSaturday) {
        String stringHour = Integer.toString(hour);
        String stringMin = Integer.toString(min);

        if(stringHour.length() == 1) {
            stringHour = "0" + stringHour;
        }

        if(stringMin.length() == 1) {
            stringMin = "0" + stringMin;
        }

        Intent myIntent = new Intent(AddNewMedActivity.this, MyAlarmService.class);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_NAME, medName);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_START_HOUR, stringHour);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_START_MIN, stringMin);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_MED_ICON_ID, iconId);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_SUNDAY, isSunday);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_MONDAY, isMonday);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_TUESDAY, isTuesday);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_WEDNESDAY, isWednesday);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_THURSDAY, isThursday);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_FRIDAY, isFriday);
        myIntent.putExtra(MyAlarmService.NOTIFICATION_IS_SATURDAY, isSaturday);

        PendingIntent pendingIntent = PendingIntent.getService(AddNewMedActivity.this,
                medName.hashCode() + hour + min,
                myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        //calendar.set(Calendar.DAY_OF_WEEK, weekday);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        Log.d(this.getLocalClassName(),"ALARM SET TO: " + calendar.getTime().toString());

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                24 * 60 * 60 * 1000, pendingIntent);
    }
*/
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

        return true;
    }

    private boolean isTimeValid(String time) {
        return !time.equals(getResources().getString(R.string.form_time_text));
    }

    private boolean isNameValid(String medName) {
        if(medName.trim().length() > 0)
            return true;

        return false;
    }
}
