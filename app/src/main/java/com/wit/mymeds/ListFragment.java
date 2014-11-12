package com.wit.mymeds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wit.mymeds.alarm.MyAlarmService;
import com.wit.mymeds.db.DbMedsEntry;
import com.wit.mymeds.db.DbMedsHelper;
import com.wit.mymeds.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * List Fragment - List of Meds
 * Created by Diogo on 26-10-2014.
 *  @author Diogo
 */
public class ListFragment extends android.support.v4.app.ListFragment {

    private static final String LIST_ITEM_TITLE = "list_title";
    private static final String LIST_ITEM_DESCRIPTION = "list_description" ;
    private static final String LIST_ITEM_HOURS = "list_hours" ;
    private static final String LIST_ITEM_ICON = "list_icon";

    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        updateListData();
    }

    private void updateListData() {
        Log.e("MYMEDS", "UPDATING LIST...");

        DbMedsHelper mDbHelper = new DbMedsHelper(getActivity());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // TODO - fragment goes to main menu when device rotates
        SimpleAdapter la = getListAdapter(db);
        if (la != null ) {
            this.setListAdapter(la);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        return view;
    }

    private SimpleAdapter getListAdapter(SQLiteDatabase db) {
        list = new ArrayList<Map<String, String>>();
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
                null,                    // The columns for the WHERE clause
                null,                    // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                sortOrder                     // The sort order
        );

        if (c.getCount() != 0) {
            c.moveToFirst();
            do {
                String itemName = c.getString(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_NAME));
                String allDays = getAllDaysString(c);
                String hours = getHoursString(c);
                Integer iconId = Constants.iconIdMap.get(c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_ICON)));
                if (iconId == null) { // If there's an error, use red icon by default
                    iconId = R.drawable.pills_red_icon;
                }
                String icon = Integer.toString(iconId);
                list.add(putData(itemName, allDays, hours, icon));

            } while (c.moveToNext());

            return new SimpleAdapter(getActivity(), list, R.layout.list_item_layout, new String[]{LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION, LIST_ITEM_HOURS, LIST_ITEM_ICON}, new int[]{R.id.list_title, R.id.list_description, R.id.list_hours, R.id.list_icon});
        }
        return null;
    }

    private String getHoursString(Cursor c) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.list_starting_hour));
        sb.append(c.getString(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_START_HOUR)));
        sb.append("  ");
        sb.append(getResources().getString(R.string.list_repeating_every));
        sb.append(c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR)));
        sb.append(getResources().getString(R.string.list_repeating_hours));
        return sb.toString();
    }

    private String getAllDaysString(Cursor c) {
        String days[] = new String[7];
        days[0] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_SUNDAY)) == 1 ? "Sunday" : null;
        days[1] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_MONDAY)) == 1 ? "Monday" : null;
        days[2] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_TUESDAY)) == 1? "Tuesday" : null;
        days[3] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY)) == 1? "Wednesday" : null;
        days[4] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_THURSDAY)) == 1? "Thursday" : null;
        days[5] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_FRIDAY)) == 1? "Friday" : null;
        days[6] = c.getInt(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_SATURDAY)) == 1? "Saturday" : null;

        String allDays = "";

        for(int i = 0; i<days.length; i++) {
            if(days[i] != null) {
                allDays = allDays + days[i] + ", ";
            }
        }

        if(allDays.length() > 0) {
            allDays = allDays.substring(0, allDays.length() - 2);
        }

        return allDays;
    }

    private void insertDbDummyValue(SQLiteDatabase db) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbMedsEntry.COLUMN_NAME_MED_NAME, "Yo Mr white");
        values.put(DbMedsEntry.COLUMN_NAME_MED_SUNDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_MONDAY, 1);
        values.put(DbMedsEntry.COLUMN_NAME_MED_TUESDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY, 1);
        values.put(DbMedsEntry.COLUMN_NAME_MED_THURSDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_FRIDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_SATURDAY, 1);
        values.put(DbMedsEntry.COLUMN_NAME_MED_START_HOUR, "TIME");
        values.put(DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR, 6);
        values.put(DbMedsEntry.COLUMN_NAME_MED_ICON, 2);


// Insert the new row, returning the primary key value of the new row
        try {
            long newRowId = db.insertOrThrow(DbMedsEntry.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(getActivity(), "An entry with the same name already exists", Toast.LENGTH_LONG).show();
        }
    }

    private HashMap<String, String> putData(String title, String description, String hours, String icon) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(LIST_ITEM_TITLE, title);
        item.put(LIST_ITEM_DESCRIPTION, description);
        item.put(LIST_ITEM_HOURS, hours);
        item.put(LIST_ITEM_ICON, icon);
        return item;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(getListView());
/*
        getListView().setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    menu.setHeaderTitle(((TextView)v.findViewById(R.id.list_title)).getText());
                    String[] menuItems = getResources().getStringArray(R.array.list_context_items);
                    for (int i = 0; i<menuItems.length; i++) {
                        menu.add(Menu.NONE, i, i, menuItems[i]);
                    }
                }
        });
*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        String selectedMed = ((TextView)((RelativeLayout) info.targetView).findViewById(R.id.list_title)).getText().toString();

        menu.setHeaderTitle(selectedMed);

        String[] menuItems = getResources().getStringArray(R.array.list_context_items);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, getResources().getIntArray(R.array.list_context_items_id)[i], i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        String selectedMed = ((TextView)((RelativeLayout) info.targetView).findViewById(R.id.list_title)).getText().toString();

        DbMedsHelper mDbHelper = new DbMedsHelper(getActivity());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //list.remove();

        if(item.getItemId() == 0) { // Edit
            
        } else if(item.getItemId() == 1) { // Delete
            Toast.makeText(this.getActivity(), "Deleting..." + selectedMed, Toast.LENGTH_SHORT).show();
            if(db.delete(DbMedsEntry.TABLE_NAME, DbMedsEntry.COLUMN_NAME_MED_NAME + "='" + selectedMed +"'", null) > 0) {
                Toast.makeText(this.getActivity(), selectedMed + " deleted", Toast.LENGTH_LONG).show();
                list.remove(info.position);
                getListView().invalidateViews();

                Intent myIntent = new Intent(getActivity(), MyAlarmService.class);

                PendingIntent pendingIntent = PendingIntent.getService(getActivity(),
                        selectedMed.hashCode(),
                        myIntent, 0);

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                Log.d("DELETING", "Alarms Canceled");
            }

        }

        return super.onContextItemSelected(item);
    }
}
