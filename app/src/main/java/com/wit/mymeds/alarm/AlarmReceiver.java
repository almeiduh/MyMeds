package com.wit.mymeds.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(MyAlarmService.NOTIFICATION_YES_ACTION.equals(action)) {
            Toast.makeText(context, "I RECEIVED THE CLICK", Toast.LENGTH_LONG).show();
        }
    }
}