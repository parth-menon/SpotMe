package com.steelsty.spotme;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

/**
 * Created by steelsty on 16/07/16.
 */

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        Log.e("start","start");
//        Intent in =new Intent(getApplicationContext(),WidgetService.class);
//        startService(in);
//        in = new Intent(getApplicationContext(),NewAppWidget.class);
//        in.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        int[] ids = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(), NewAppWidget.class));
//        in.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
//        sendBroadcast(in);
        MultiDex.install(MyApp.this);
    }
}

