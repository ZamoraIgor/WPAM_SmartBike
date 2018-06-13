package com.wpam.smartbike;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    /*
    *
    *
    *
    * https://github.com/ZamoraIgor/WPAM_SmartBike
    *
    *
    *
    */

    String deviceData;
    TextView status_info;

    public void toast(Context context, String text, int duration) {
        Toast myToast = Toast.makeText(context, text, duration);
        myToast.show();
    }
    public void initializeData() {
        deleteSharedPreferences(deviceData);
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("range", 0);
        editor.putFloat("battery_voltage", 0F);
        editor.putFloat("current_speed", 0F);
        editor.putFloat("current_power", 0F);
        editor.putString("connection_status", "Not connected");
        editor.putFloat("set_deadtime", 0F);
        editor.putFloat("set_source_current", 0F);
        editor.putFloat("set_sink_current", 0F);
        editor.putFloat("distance_covered", 0F);
        editor.apply();
    }

    public void setStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        status_info= (TextView) findViewById(R.id.status);
        status_info.setText(sharedPreferences.getString("connection_status", ""));
    }
    public void changeStatus(String new_status){
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("connection_status", new_status);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializeData();
        setContentView(R.layout.activity_main);
        setStatus();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent previousIntent = getIntent();
        deviceData = previousIntent.getStringExtra("deviceData");
        setStatus();
    }

    public void clickMaps(View view){
        Intent myIntent = new Intent(this, GoogleMapsActivity.class);
        myIntent.putExtra("deviceData", deviceData);
        startActivity(myIntent);
    }

    public void clickConnect(View view){
        Intent myIntent = new Intent(this, ConnectActivity.class);
        myIntent.putExtra("deviceData", deviceData);
        startActivity(myIntent);
    }

    public void clickStats(View view) {
        Intent myIntent = new Intent(this, StatsActivity.class);
        myIntent.putExtra("deviceData", deviceData);
        startActivity(myIntent);
    }
    public void clickSettings(View view){
        Intent myIntent = new Intent(this, SettingsActivity.class);
        myIntent.putExtra("deviceData", deviceData);
        startActivity(myIntent);
    }


}