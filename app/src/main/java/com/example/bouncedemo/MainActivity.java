package com.example.bouncedemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.bouncedemo.GetLocation.RequestPermissionCode;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback , LocationListener,GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public double currentLatitude;
    public static final int ROUND = 10;
    public double currentLongitude;
    Button bus,police,hospital,petrolpump;
    private GoogleMap mMap;
    public GoogleApiClient googleApiClient;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    Marker marker;
    public FusedLocationProviderClient fusedLocationProviderClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //defining the buttons
        petrolpump = findViewById(R.id.petrolpump);
        bus = findViewById(R.id.bus);
        police = findViewById(R.id.police);
        hospital = findViewById(R.id.hospital);

        //calling google api's client to establish location connections
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //fusedLocationProviderClient provides the current location coordinates
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        NotificationChannel channel = new NotificationChannel("MyNotification","MyNotification", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
        mUsers= FirebaseDatabase.getInstance().getReference("coordinates");
        mUsers.push().setValue(marker);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.google_style));
            if (!success) {
                // Handle map style load failure
                Log.e("map_style","map style updated please do check it");
            }
        } catch (Resources.NotFoundException e) {
            // Oops, looks like the map style resource couldn't be found!
            Log.e("map_style","map is not updated yet ... do some other stuff");
        }

        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(R.raw.google_style);

        //setting the size of marker in map by using Bitmap Class
        int height = 80;
        int width = 80;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.scooter);
        Bitmap b=bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    UserInformation user = s.getValue(UserInformation.class);
                    LatLng location=new LatLng(Double.parseDouble(user.latitude),Double.parseDouble(user.longitude));
                    mMap.addMarker(new MarkerOptions().position(location).title(user.name)).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();               //getting current latitude
        currentLongitude = location.getLongitude();             //getting current longitude

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();              //establishment of google api client connection
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();       //discarding the google api client service if the activity is in onStop condition
        }
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()                             //Through this we are fetching the last location of user recorded
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                //making button clickable and to open the maps with nearby bus and metro stations from your current location
                                bus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String x = String.valueOf(location.getLatitude());
                                                String y = String.valueOf(location.getLongitude());

                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?&saddr="
                                                    + x                                                 //passing the coordinates as default parameters in maps Intent as source
                                                    + ","
                                                    + y
                                                    + "&daddr=nearby bus or metro station"              //passing the destination

                                            ));
                                                startActivity(intent);

                                            }
                                });

                                //making button clickable and to open the maps with nearby bus and metro stations from your current location
                                        police.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String x = String.valueOf(location.getLatitude());
                                        String y = String.valueOf(location.getLongitude());

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?&saddr="
                                                        + x
                                                        + ","
                                                        + y
                                                        + "&daddr=nearby police station"

                                                ));
                                        startActivity(intent);
                                            }
                                        });

                                //making button clickable and to open the maps with nearby bus and metro stations from your current location
                                        hospital.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String x = String.valueOf(location.getLatitude());
                                                String y = String.valueOf(location.getLongitude());

                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?&saddr="
                                                        + x
                                                        + ","
                                                        + y
                                                        + "&daddr=nearby hospitals"
                                                        ));
                                        startActivity(intent);

                                            }
                                        });

                                        petrolpump.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String x = String.valueOf(location.getLatitude());
                                                String y = String.valueOf(location.getLongitude());

                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?&saddr="
                                                                + x
                                                                + ","
                                                                + y
                                                                + "&daddr=nearby petrol pump"
                                                        ));
                                                startActivity(intent);

                                            }
                                        });
                            }
                        }
                    });
        }

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}