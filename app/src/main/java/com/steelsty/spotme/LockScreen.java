package com.steelsty.spotme;

import android.content.Context;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class LockScreen extends AppCompatActivity {
    PowerManager pm;
    PowerManager.WakeLock wl;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock_screen);
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "");
        wl.acquire();
        t= new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(6000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(wl.isHeld())
                        wl.release();
                    finish();
                }
            }
        };
        t.start();
    }
}
