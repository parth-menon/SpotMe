package com.steelsty.spotme;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.ShareActionProvider.*;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    GoogleMap mMap;
    int mcc=0,mnc=0,cid=0,lac=0;
    Button b;
    boolean running=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b=(Button) findViewById(R.id.button);
        b.setOnClickListener(this);
        if(Globals.check(getApplicationContext())){
            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        99);
            }
            else if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        98);

            }
            else
            {
                Globals.phone=1; Globals.loc=1;
            }
        }
        else
        {
            Globals.phone=1;
            Globals.loc=1;
        }
        if(Globals.isset())
        {
            Intent in =new Intent(getApplicationContext(),WidgetService.class);
            startService(in);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            running=false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Globals.phone=1;
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                98);
                    }
                    return;
                }
                return;
            }
            case 98: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Globals.loc=1;
                    return;

                }
                return;
            }
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
    }
    @Override
    public void onClick(View v) {
        LatLng place = new LatLng(Globals.lat,Globals.lng);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(place).title(Globals.address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        if(running==false) {
            Toast.makeText(getApplicationContext(), "Getting Location", Toast.LENGTH_LONG).show();
            TelephonyManager tv = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            GsmCellLocation gc = (GsmCellLocation) tv.getCellLocation();
            Globals.cid = gc.getCid();
            Globals.lac = gc.getLac();
            String networkOperator = tv.getSimOperator();
            if (TextUtils.isEmpty(networkOperator) == false)
            {
                Globals.mcc = Integer.parseInt(networkOperator.substring(0, 3));
                Globals.mnc = Integer.parseInt(networkOperator.substring(3));
            }
            DTask k = new DTask();
            running = true;
            k.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "LatLng: "+Globals.lat+","+Globals.lng+"\nAddress: "+Globals.address+"\n");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share"));
                return true;
            case R.id.alarm:
                Intent alarmIntent = new Intent(MainActivity.this,AlarmActivity.class);
                startActivity(alarmIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getApplicationContext(),Globals.address,Toast.LENGTH_LONG).show();
    }

    private class DTask extends AsyncTask<String, String, String> {
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                if(!result.equals("")) {
                    String data[] = result.split(",");
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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
                    Intent in = new Intent(getApplicationContext(),NewAppWidget.class);
                    in.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), NewAppWidget.class));
                    in.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                    sendBroadcast(in);
                    LatLng place = new LatLng(Globals.lat,Globals.lng);
                    running=false;
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(place).title(Globals.address));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                }
            }catch(Exception e){
                e.printStackTrace();
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
}
