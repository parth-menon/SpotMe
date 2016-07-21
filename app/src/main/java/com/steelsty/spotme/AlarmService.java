package com.steelsty.spotme;

import android.Manifest;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class AlarmService extends Service {
    PowerManager pm;
    PowerManager.WakeLock wl;
    Context c;
    int id=0;
    DbUtil db;
    public AlarmService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if(Globals.isset())
        {
            try {
                pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
                wl.acquire();
                db=new DbUtil(getApplicationContext());
                c = getApplicationContext();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
            this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            TelephonyManager tv = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            GsmCellLocation gc = (GsmCellLocation) tv.getCellLocation();
            Globals.cid = gc.getCid();
            Globals.lac = gc.getLac();
            id=intent.getIntExtra("id",0);
            Log.e("id",id+"");
            String networkOperator = tv.getSimOperator();
            if (TextUtils.isEmpty(networkOperator) == false) {
                Globals.mcc = Integer.parseInt(networkOperator.substring(0, 3));
                Globals.mnc = Integer.parseInt(networkOperator.substring(3));
            }
            ATask k = new ATask();
            k.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ATask extends AsyncTask<String, String, String> {
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl();
            } catch (Exception e) {
                if (wl.isHeld())
                    wl.release();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                if(!result.equals("")) {
                    String addr=db.addressID(id);
                    String arr[]=addr.split(",");


                    String data[] = result.split(",");
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(c, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.parseDouble(data[0]), Double.parseDouble(data[1]), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Globals.lat=Double.parseDouble(data[0]);
                    Globals.lng= Double.parseDouble(data[1]);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String area = addresses.get(0).getAddressLine(0);
                    String a[]=area.split(",");
                    area=a[(a.length)-1].trim();
                    Log.e("area",  a[(a.length)-1]);
                    result=area+",\n"+city+",\n"+state;
                    Globals.address=result;
                    if(arr.length==3){
                        String p=arr[0];
                        String c = arr[1].trim();
                        String s=arr[2].trim();
                        if(s.equals(state)&&c.equals(city)&&(p.contains(area)||area.contains(p))){
                            Intent i = new Intent(getApplicationContext(),LockScreen.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("id",id);
                            db.setActive(id);
                            startActivity(i);
                        }
                    }
                    else {
                        String c = arr[0].trim();
                        String s = arr[1].trim();
                        if (s.equals(state) && c.equals(city)) {
                            Intent i = new Intent(getApplicationContext(), LockScreen.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("id",id);
                            db.setActive(id);
                            startActivity(i);
                        }
                    }
                    Intent in = new Intent(getApplicationContext(),NewAppWidget.class);
                    in.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), NewAppWidget.class));
                    in.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                    sendBroadcast(in);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            finally {
                if (wl.isHeld())
                    wl.release();
                AlarmService.this.stopSelf();
            }
        }
        private String downloadUrl() throws ProtocolException {
            String d = "";
            try {
                String ur = "http://www.open-electronics.org/celltrack/cell.php?hex=0&";
                String urlParameters = "mcc=" + Globals.mcc +
                        "&mnc=" +
                        Globals.mnc +
                        "&lac=" +
                        Globals.lac +
                        "&cid=" +
                        Globals.cid;
                URL url = new URL(ur + urlParameters);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                    if(line.contains("lat=\""))
                        break;
                }
                d=responseOutput.toString();
                br.close();
            } catch (MalformedURLException e) {
                Log.e("error", "1");
            } catch (IOException e) {
                Log.e("error", "2");
            }
            try {
                String dat[] = d.split("lat=\"");
                dat = dat[1].split("rag");
                String dat2[] = dat[0].split("\"");
                String lat = dat2[0];
                dat = dat[0].split("lng=\"");
                dat2 = dat[1].split("\"");
                String lng = dat2[0];
                Log.e("lat",lat);
                Log.e("lng",lng);
                d=lat+","+lng;
            }catch(Exception e){
                e.printStackTrace();
            }
            return d;
        }
    }

    @Override
    public boolean stopService(Intent name) {
        if(wl.isHeld())
            wl.release();
        return super.stopService(name);
    }
}
