package com.steelsty.spotme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Vector;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            DbUtil db = new DbUtil(context);

            Vector<Vector<String>> vo=db.getAlarms();
            int len=vo.size();
            for(int i=0;i<len;i++) {
                Vector<String> v = vo.get(i);
                int id=Integer.parseInt(v.get(0));
                String time=v.get(2);
                String date=v.get(3);
                int active=Integer.parseInt(v.get(4));
                if (active == 1) {
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    String temp[] =time.split(":");
                    String hour=temp[0];
                    String temp1[]=temp[1].split(" ");
                    String min=temp1[0];
                    String ampm=temp1[1];
                    int hr=0,mins=0,day=0,month=0,year=0;
                    if(ampm.equals("AM"))
                        if(hour.equals("12"))
                            hr=0;
                        else
                            hr=Integer.parseInt(hour);
                    else if(ampm.equals("PM"))
                        if(hour.equals("12"))
                            hr=12;
                        else
                            hr=Integer.parseInt(hour)+12;
                    mins=Integer.parseInt(min);
                    temp=date.split("/");
                    day=Integer.parseInt(temp[0]);
                    month=Integer.parseInt(temp[1]);
                    year=Integer.parseInt(temp[2]);

                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.HOUR_OF_DAY,hr);
                    calendar.set(Calendar.MINUTE,mins);
                    calendar.set(Calendar.DAY_OF_MONTH,day);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.YEAR,year);
                    manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            Toast.makeText(context,"Location alarms are set.",Toast.LENGTH_LONG).show();
        }
    }
}
