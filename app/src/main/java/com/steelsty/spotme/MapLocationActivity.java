package com.steelsty.spotme;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {
    EditText ed;
    String city="",state="",place="";
    Button find;
    GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        ed = (EditText) findViewById(R.id.editMap);
        find=(Button) findViewById(R.id.findMap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        find.setOnClickListener(this);

    }
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String s = ed.getText().toString();
        ETask k = new ETask();
        k.execute(s);
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Globals.city = city;
        Globals.state = state;
        Globals.place=place;
        finish();
    }

    private class ETask extends AsyncTask<String, Address, Address> {
        Address ad = null;
        @Override
        protected Address doInBackground(String... url) {
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                addresses = geocoder.getFromLocationName(url[0], 1);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                place = addresses.get(0).getAddressLine(0);
//                Log.e("address",place);
                if(place.contains(state)||place.contains(city)){
                    place="";
                }else{
                    String a[]=place.split(",");
                    place=a[(a.length)-1].trim();}
                ad=addresses.get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ad;
        }
        @Override
        protected void onPostExecute(Address result) {
            try {
                mMap.clear();
                LatLng la =new LatLng(result.getLatitude(),result.getLongitude());
                mMap.addMarker(new MarkerOptions().position(la).title("Select"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(la));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}