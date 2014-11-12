package com.wit.mymeds;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String stringHour = Integer.toString(hourOfDay);
        String stringMin = Integer.toString(minute);
        if(stringHour.length() == 1) {
            stringHour = "0" + stringHour;
        }

        if(stringMin.length() == 1) {
            stringMin = "0" + stringMin;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM HH:mm");

        ((TextView)getActivity().findViewById(R.id.form_time_text)).setText(sdf.format(cal.getTime()));

        // change seek bar limit
        //((SeekBar)getActivity().findViewById(R.id.seekBarRepeatHours)).setMax((23-hourOfDay));
    }
}