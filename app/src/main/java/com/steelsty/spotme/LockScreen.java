package com.steelsty.spotme;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Vector;

public class LockScreen extends AppCompatActivity {
    PowerManager pm;
    PowerManager.WakeLock wl;
    Thread t;
    private MediaPlayer mp;
    TextView tv;
    Button bt;
    DbUtil db;
    int id=0;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private Ringtone ringtone;
    private boolean isRinging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_lock_screen);
        try {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "");
            wl.acquire();
            tv = (TextView) findViewById(R.id.textViewPlace);
            bt = (Button) findViewById(R.id.buttonCancel);
            db = new DbUtil(this);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            id=getIntent().getIntExtra("id", 0);
            Vector<String> v = db.alarm(id);
            tv.setText(v.get(1));
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteAlarmsId(id);
                    finish();
                }
            });
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            if (volume == 0)
                volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            if (ringtone != null) {
                ringtone.setStreamType(AudioManager.STREAM_ALARM);
                ringtone.play();
                isRinging = true;
            }

            long[] pattern = {0, 1000, 2000};
            vibrator.vibrate(pattern, 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(wl.isHeld())
            wl.release();
        if(isRinging)
            ringtone.stop();
        if(ringtone!=null && ringtone.isPlaying())
            ringtone.stop();
        try {
            vibrator.cancel();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
