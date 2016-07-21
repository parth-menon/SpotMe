package com.steelsty.spotme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class AlarmReceiver extends BroadcastReceiver {
    DbUtil db;
    Context c=null;
    AlarmManager manager;
    PowerManager pm;
    PowerManager.WakeLock wl;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            db = new DbUtil(context);
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            c = context;
            int id = intent.getIntExtra("id", 0);
            Vector<String> v = db.alarm(id);
            if (v.size() != 0) {
                int active = Integer.parseInt(v.get(4));
                if (active == 1) {
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("id", id);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.MINUTE, 2);
                    Log.e("set", calendar.getTime().toString());
                    manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        Intent i = new Intent(context,LockScreen.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
                    Intent ser = new Intent(context, AlarmService.class);
                    ser.putExtra("id", id);
                    context.startService(ser);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(wl.isHeld())
                wl.release();
        }
    }
}
