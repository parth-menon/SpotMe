package com.steelsty.spotme;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NewAlarm extends AppCompatActivity implements View.OnClickListener {
    TextView placeTView;
    Button find,setalarm;
    EditText time,date;
    DbUtil db;
    DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day,hour,min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        db = new DbUtil(NewAlarm.this);
        placeTView=(TextView) findViewById(R.id.textView2);
        find=(Button) findViewById(R.id.find);
        setalarm=(Button) findViewById(R.id.setalarm);
        time=(EditText) findViewById(R.id.time);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,1);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min =calendar.get(Calendar.MINUTE);
                setTime(time);
                return false;
            }
        });
        date=(EditText) findViewById(R.id.date);
        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                setDate(date);
                return false;
            }
        });
        showTime(hour,min);
        showDate(year,month,day);
        find.setOnClickListener(this);
        setalarm.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(Globals.city.equals("") && Globals.state.equals("")))
            placeTView.setText(Globals.city+",\n"+Globals.state);
        else if(!(Globals.place.equals("") && Globals.city.equals("") && Globals.state.equals("")))
            placeTView.setText(Globals.place+",\n"+Globals.city+",\n"+Globals.state);
        else
            placeTView.setText("");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.find:
                Intent in = new Intent(NewAlarm.this, MapLocationActivity.class);
                startActivity(in);
                break;
            case R.id.setalarm:
                if(!placeTView.getText().equals(""))
                {
                    int id = db.alarmID();
                    String p=placeTView.getText().toString(),t=time.getText().toString(),d=date.getText().toString();
                    db.insertAlarm(id,p,t,d,1);
                    Globals.place=""; Globals.city=""; Globals.state="";
                    Toast.makeText(getApplicationContext(),"Alarm is set",Toast.LENGTH_LONG).show();
                    Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    alarmIntent.putExtra("id",id);
                    PendingIntent pendingIntent =PendingIntent.getBroadcast(getApplicationContext(), id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND,0);
                    calendar.set(Calendar.MINUTE,min);
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.DAY_OF_MONTH,day);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.YEAR,year);
//                    Log.e("Fetch Set for :",calendar.getTime().toString());
                    manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Fill all entries",Toast.LENGTH_LONG).show();
                }
        }
    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(998);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        if(id==998){
            return new TimePickerDialog(this,myTimeListener,hour,min,false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2+1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            showTime(hourOfDay,minutes);

        }
    };

    private void showDate(int year, int month, int day) {
        this.year=year;
        this.month=month;
        this.day=day;
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
    private void showTime(int hours, int mins) {
        this.hour=hours;
        this.min=mins;
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();
        time.setText(aTime);
    }
}
