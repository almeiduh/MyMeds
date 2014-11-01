package com.wit.mymeds;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        
        View list = findViewById(R.id.testlist);
        
        ArrayAdapter<String> aadpter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.os));
        //new ArrayAdapter<String>(this, R.array.os);
        
        ((ListView) list).setAdapter(aadpter);
        
    }
}
