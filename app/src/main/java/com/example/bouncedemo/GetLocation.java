package com.example.bouncedemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GetLocation extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    public static final int RequestPermissionCode = 1;
    protected GoogleApiClient googleApiClient;
    protected TextView longitudeText;
    protected TextView latitudeText;
    protected Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    CardView nearbyhospital,nearbymedicalshop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        nearbyhospital = (CardView)findViewById(R.id.nearbyhospital);
        nearbymedicalshop = (CardView)findViewById(R.id.nearbymedicalshop);
        longitudeText = (TextView) findViewById(R.id.longitude_text);
        latitudeText = (TextView) findViewById(R.id.latitude_text);
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
//                                latitudeText.setText(String.valueOf(location.getLatitude()));
//                                longitudeText.setText(String.valueOf(location.getLongitude()));
//
//
//                                nearbyhospital.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        String x = String.valueOf(location.getLatitude());
//                                        String y = String.valueOf(location.getLongitude());
//
//                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                                                Uri.parse("http://maps.google.com/maps?&saddr="
//                                                        + x
//                                                        + ","
//                                                        + y
//                                                        + "&daddr=nearby hospitals"
//
//                                                ));
//                                        startActivity(intent);
//
//                                    }
//                                });
//
//                                nearbymedicalshop.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        String x = String.valueOf(location.getLatitude());
//                                        String y = String.valueOf(location.getLongitude());
//
//                                        Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW,
//                                                Uri.parse("http://maps.google.com/maps?&saddr="
//                                                        + x
//                                                        + ","
//                                                        + y
//                                                        + "&daddr=nearby medical shops"
//
//                                                ));
//                                        startActivity(intent1);
//                                    }
//                                });


                            }
                        }
                    });
        }

    }



    private void requestPermission() {
        ActivityCompat.requestPermissions(GetLocation.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity", "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "Connection suspendedd");
    }

}