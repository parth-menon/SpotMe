package com.steelsty.spotme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap mMap;
    int mcc=0,mnc=0,cid=0,lac=0;
    Button b;
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
        }
        else{
            finish();
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
    }
    @Override
    public void onClick(View v) {
        LatLng place = new LatLng(Globals.lat,Globals.lng);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(place).title(Globals.address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
    }
}
