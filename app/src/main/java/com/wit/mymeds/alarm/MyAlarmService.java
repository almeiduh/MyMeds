package com.wit.mymeds.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.wit.mymeds.MainActivity;
import com.wit.mymeds.R;
import com.wit.mymeds.utils.Constants;

import java.util.Calendar;


public class MyAlarmService extends Service {

    public static final String NOTIFICATION_MED_NAME = "NOTIFICATION_MED_NAME";
    public static final String NOTIFICATION_MED_ICON_ID = "NOTIFICATION_MED_ICON_ID";
    public static final String NOTIFICATION_IS_SUNDAY = "NOTIFICATION_IS_SUNDAY";
    public static final String NOTIFICATION_IS_MONDAY = "NOTIFICATION_IS_MONDAY";
    public static final String NOTIFICATION_IS_TUESDAY = "NOTIFICATION_IS_TUESDAY";
    public static final String NOTIFICATION_IS_WEDNESDAY = "NOTIFICATION_IS_WEDNESDAY";
    public static final String NOTIFICATION_IS_THURSDAY = "NOTIFICATION_IS_THURSDAY";
    public static final String NOTIFICATION_IS_FRIDAY = "NOTIFICATION_IS_FRIDAY";
    public static final String NOTIFICATION_IS_SATURDAY = "NOTIFICATION_IS_SATURDAY";
    public static final String PREFS_NAME = "NOTIFICATION_PREFS";

    public static String NOTIFICATION_YES_ACTION = "NOTIFICATION_YES_ACTION";

    @Override
    public void onCreate() {
    }

    @Override

    public IBinder onBind(Intent intent) {
        return null;

    }



    @Override

    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean notificationsEnabled = prefs.getBoolean("settings_notification_enabled", false);
        boolean smsWarningEnabled = prefs.getBoolean("settings_sms_warning_enabled", false);
        int smsTimeout =  prefs.getInt("settings_sms_warning_sms_timeout", 0);
        String smsWarningPhoneNumber =  prefs.getString("settings_sms_warning_sms_number", "");

        if(notificationsEnabled) {
            Bundle extras = intent.getExtras();

            String medName = extras.getString(NOTIFICATION_MED_NAME);

            int iconId = (int) extras.getLong(NOTIFICATION_MED_ICON_ID);

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            boolean isActiveToday = false;

            switch (day) {
                case 1:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_SUNDAY);
                    break;
                case 2:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_MONDAY);
                    break;
                case 3:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_TUESDAY);
                    break;
                case 4:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_WEDNESDAY);
                    break;
                case 5:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_THURSDAY);
                    break;
                case 6:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_FRIDAY);
                    break;
                case 7:
                    isActiveToday = extras.getBoolean(NOTIFICATION_IS_SATURDAY);
                    break;
            }

            if (isActiveToday) {

                iconId = Constants.iconIdMap.get(iconId);

                int notificationId = medName.hashCode();

                Intent yesReceive = new Intent();
                yesReceive.setAction(NOTIFICATION_YES_ACTION);
                yesReceive.putExtra("NOTIFICATION_MED_NAME", medName);
                yesReceive.putExtra("NOTIFICATION_ID", notificationId);

                Log.d(getClass().getName(), "Created Notification ID: " + notificationId);

                PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, notificationId,
                        yesReceive, 0);

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(iconId)
                                .setContentTitle(medName)
                                .setContentText(getResources().getString(R.string.notification_description))
                                .setSound(soundUri)
                                .setOngoing(true)
                                .addAction(R.drawable.ic_action_accept, "OK", pendingIntentYes);


                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);

                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // mId allows you to update the notification later on.
                mNotificationManager.notify(notificationId, mBuilder.build());

                Log.d(getClass().getName(), "Created Notification ID: " + notificationId);

                // Add notification status to
                SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean(medName, true);

                // Commit the edits!
                editor.commit();

                if (smsWarningEnabled) {
                    Handler hdl = new Handler();
                    if(smsTimeout != 0) {
                        int delay = smsTimeout * 60 * 1000;
                        hdl.postDelayed(new SmsRunnable(medName, smsWarningPhoneNumber), delay);
                    } else {
                        Log.e("MYMEDS", "INVALID SMS TIMOUT");
                    }
                }
            } else {
                Log.d(this.getClass().getName(), "ALARM NOT ACTIVE FOR THIS DAY/TIME");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class SmsRunnable implements Runnable {
        private String medName;
        private String phoneNumber;

        public SmsRunnable(String medName, String phoneNumber) {
            this.medName = medName;
            this.phoneNumber = phoneNumber;
        }

        public void run() {
            SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, 0);
            boolean isNotificationActive = sharedPrefs.getBoolean(medName, false);
            if (isNotificationActive) {
                PendingIntent pi =
                        PendingIntent.
                                getActivity(getApplicationContext(),
                                        0,
                                        new Intent(getApplicationContext(), MyAlarmService.class),
                                        0);
                SmsManager sms = SmsManager.getDefault();
                if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
                    sms.sendTextMessage(phoneNumber, null,
                            getResources().getString(R.string.sms_message), pi, null);
                    Toast.makeText(getApplicationContext(), "SMS sent to the Warning Number", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MYMEDS", "INVALID PHONE NUMBER");
                }/*
                if(!phoneNumber.equals(getResources().getString(R.string.settings_sms_warning_sms_number_summary))) {

                } else {
                }*/
            }
        }
    }
}


