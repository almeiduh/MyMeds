package com.wit.mymeds;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.wit.mymeds.db.DbMedsEntry;
import com.wit.mymeds.db.DbMedsHelper;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        DbMedsHelper mDbHelper = new DbMedsHelper(getActivity());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //insertDbDummyValue(db);


        ListAdapter la = getListAdapter(db);

        this.setListAdapter(la);

        return view;
    }

    private ListAdapter getListAdapter(SQLiteDatabase db) {
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
                DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR
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


        c.moveToFirst();
        do {
            String itemName = c.getString(c.getColumnIndexOrThrow(DbMedsEntry.COLUMN_NAME_MED_NAME));
            String allDays = getAllDaysString(c);
            list.add(putData(itemName, allDays));

        } while(c.moveToNext());

        return new SimpleAdapter(getActivity(), list, R.layout.list_item_layout, new String[] {LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION } , new int[]{R.id.list_title, R.id.list_description} );
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
        values.put(DbMedsEntry.COLUMN_NAME_MED_NAME, "Bazinga");
        values.put(DbMedsEntry.COLUMN_NAME_MED_SUNDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_MONDAY, 1);
        values.put(DbMedsEntry.COLUMN_NAME_MED_TUESDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_WEDNESDAY, 1);
        values.put(DbMedsEntry.COLUMN_NAME_MED_THURSDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_FRIDAY, 0);
        values.put(DbMedsEntry.COLUMN_NAME_MED_SATURDAY, 1);
        values.put(DbMedsEntry.COLUMN_NAME_MED_START_HOUR, "TIME");
        values.put(DbMedsEntry.COLUMN_NAME_MED_FREQUENCY_HOUR, 6);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DbMedsEntry.TABLE_NAME, null, values);
    }

    private HashMap<String, String> putData(String name, String purpose) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(LIST_ITEM_TITLE, name);
        item.put(LIST_ITEM_DESCRIPTION, purpose);
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
                Toast.makeText(this.getActivity(), "Deleted." + selectedMed, Toast.LENGTH_LONG).show();
                list.remove(info.position);
                getListView().invalidateViews();
            }

        }

        return super.onContextItemSelected(item);
    }
}
