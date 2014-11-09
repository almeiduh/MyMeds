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

import java.util.Calendar;


public class AddNewMedActivity extends ActionBarActivity {

    private PendingIntent pendingIntent;

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

        setupAlarm();

        // close this activity
        setResult(Activity.RESULT_OK);
        this.finish();
    }

    private void setupAlarm() {
        Intent myIntent = new Intent(AddNewMedActivity.this, MyAlarmService.class);
        pendingIntent = PendingIntent.getService(AddNewMedActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND, 10);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(AddNewMedActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
    }

    private boolean areAllFormFieldsValid() {
        String medName = ((EditText)findViewById(R.id.form_name_edit)).getText().toString();
        if(isNameValid(medName)) {
            return true;
        }
        Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isNameValid(String medName) {
        if(medName.trim().length() > 0)
            return true;

        return false;
    }
}
