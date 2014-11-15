package com.wit.mymeds.alarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(this.getClass().getName(), "onReceive()");
        if(MyAlarmService.NOTIFICATION_YES_ACTION.equals(action)) {
            Log.d(this.getClass().getName(), "getting notification");

            Bundle extras = intent.getExtras();

            String medName = extras.getString("NOTIFICATION_MED_NAME");
            Log.d(getClass().getName(), "medName: " + medName);

            int notificationId = extras.getInt("NOTIFICATION_ID");
            Log.d(getClass().getName(), "Received notificationId: " + notificationId);

            NotificationManager notificationManager =
                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(notificationId);

            Log.d(this.getClass().getName(), "notification cleared");

            // Add notification status to
            SharedPreferences settings = context.getSharedPreferences(MyAlarmService.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(medName);

            Log.d(this.getClass().getName(), "editing preferences");

            // Commit the edits!
            editor.commit();
        }
    }
}