package com.wit.mymeds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.preference.PreferenceFragment;
import android.support.v4.view.ViewCompatKitKat;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.internal.widget.ViewUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.wit.mymeds.utils.Constants;

import java.util.concurrent.atomic.AtomicInteger;


public class SettingsFragment extends PreferenceFragment {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ActionMenuItemView item = (ActionMenuItemView) getActivity().findViewById(R.id.add_med_button);
        if(item != null)
            item.setVisibility(View.INVISIBLE);

        // SMS NUMBER
        Preference smsNumberPref= (Preference) findPreference("settings_sms_warning_sms_number");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String smsNumberCurrentValue = prefs.getString("settings_sms_warning_sms_number",
                getResources().getString(R.string.settings_sms_warning_sms_number_summary));

        smsNumberPref.setSummary(smsNumberCurrentValue);

        smsNumberPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                return true;
            }
        });


        //SMS TIMEOUT
        Preference smsTimeoutPref= (Preference) findPreference("settings_sms_warning_sms_timeout");

        int smsTimeoutCurrentValue = prefs.getInt("settings_sms_warning_sms_timeout", 0);

        if(smsTimeoutCurrentValue == 0) {
            smsTimeoutPref.setSummary(getResources().getString(R.string.settings_sms_warning_sms_timeout_title));
        } else {
            smsTimeoutPref.setSummary(String.valueOf(smsTimeoutCurrentValue));
        }
        smsTimeoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.number_picker_dialog_title);

                final NumberPicker np = new NumberPicker(getActivity());

                String[] values=new String[24];
                for(int i=0;i<values.length;i++){
                    values[i]=Integer.toString((i+1)*5);
                }

                np.setMaxValue(values.length-1);
                np.setMinValue(0);
                np.setDisplayedValues(values);
                final int viewId = generateViewId();
                np.setId(viewId);
                builder.setView(np);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pickedValue = (((NumberPicker)((AlertDialog)dialog).findViewById(viewId)).getValue() +1) * 5;

                        Preference smsTimeoutPref= (Preference) findPreference("settings_sms_warning_sms_timeout");
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putInt("settings_sms_warning_sms_timeout", pickedValue);
                        editor.commit();
                        editor.apply();

                        smsTimeoutPref.setSummary(String.valueOf(pickedValue));
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();


                return true;
            }
        });
    }


    @Override
    public void onDestroy() {
        ActionMenuItemView item = (ActionMenuItemView) getActivity().findViewById(R.id.add_med_button);
        if(item != null)
            item.setVisibility(View.VISIBLE);
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        Log.e("AA", "" + reqCode);

        // Check which request we're responding to
        if (reqCode == SettingsFragment.PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                Uri contactUri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                Preference myPref= (Preference) findPreference("settings_sms_warning_sms_number");
                myPref.setSummary(number);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("settings_sms_warning_sms_number", number);
                editor.commit();
                editor.apply();
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("settings_sms_warning_sms_number")) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
