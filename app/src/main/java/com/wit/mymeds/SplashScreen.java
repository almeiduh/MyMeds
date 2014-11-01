/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wit.mymeds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 *
 * @author jpedro
 */
public class SplashScreen extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
        Log.d("SplashScreen", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                Log.d("SplashScreen", "handleMessage");
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
            
        };
        
        handler.sendMessageDelayed(new Message(), 1500L);
    }
    
}
