package com.wpam.smartbike;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;



public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, android.location.LocationListener {

    TextView range_val;
    TextView dist_val;
    TextView altitude_val;
    float actual_dist=0F;
    float actual_alt=0F;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String deviceData;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastLocation;

    public void initialize_variables(){
        range_val= (TextView) findViewById(R.id.range_val);
        dist_val= (TextView) findViewById(R.id.distance_val);
        altitude_val= (TextView) findViewById(R.id.altitude_val);

        Intent previousIntent = getIntent();
        deviceData = previousIntent.getStringExtra("deviceData");
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        range_val.setText(sharedPreferences.getFloat("range", 0)+" m");
        dist_val.setText(Float.toString(actual_dist)+ " m");
        altitude_val.setText(Float.toString(actual_alt)+ " m");
    }
    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
                placeRangeCircle(currentLocation);

            }
        }


    }
    protected void placeMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        mMap.addMarker(markerOptions);
    }
    public void placeRangeCircle(LatLng location){
     CircleOptions circleOptions = new CircleOptions().center(location);
     SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
     circleOptions.radius(sharedPreferences.getFloat("range", 0));
     circleOptions.fillColor(0x1F0000FF);
     circleOptions.strokeColor(0xFF0000FF);
     circleOptions.strokeWidth(3F);
     mMap.addCircle(circleOptions);
    }
    public LatLng getCurrentLocation() {
        LatLng temp = new LatLng(mLastLocation.getLatitude(), mLastLocation
                .getLongitude());
        return temp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        initialize_variables();
    }

    @Override
    public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
        mMap.clear();
        placeRangeCircle(getCurrentLocation());
        placeMarkerOnMap(latLng);
        actual_dist=calculatedistance(getCurrentLocation(), latLng);
        dist_val.setText(Float.toString(actual_dist)+ " m");

    }

    private Float calculatedistance(LatLng currentLocation, LatLng latLng) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(currentLocation.latitude - latLng.latitude);
        double lonDistance = Math.toRadians(currentLocation.longitude - latLng.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(currentLocation.latitude)) * Math.cos(Math.toRadians(latLng.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return (float)distance;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);


    }

    @Override
    public void onLocationChanged(Location location) {

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
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }



}
