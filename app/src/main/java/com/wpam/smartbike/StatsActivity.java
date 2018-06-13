package com.wpam.smartbike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class StatsActivity extends AppCompatActivity {

    String deviceData;
    TextView distance_covered, speed_average, power_average, energy_saved, reduced_co2, calories_burned;
    TextView battery_voltage, range_left, deadtime, sink_current, source_current;
    float float_energy_saved, float_reduced_co2, float_calories_burned;

    public void toast(Context context, String text, int duration) {
        Toast myToast = Toast.makeText(context, text, duration);
        myToast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Intent previousIntent = getIntent();
        deviceData = previousIntent.getStringExtra("deviceData");

        distance_covered= (TextView) findViewById(R.id.distance_covered);
        speed_average=(TextView) findViewById(R.id.speed_average);
        power_average= (TextView) findViewById(R.id.power_average);
        energy_saved=(TextView) findViewById(R.id.energy_saved);
        reduced_co2= (TextView) findViewById(R.id.reduced_co2);
        calories_burned=(TextView) findViewById(R.id.calories_burned);
        battery_voltage=(TextView) findViewById(R.id.battery_voltage);
        range_left= (TextView) findViewById(R.id.range_left);
        deadtime=(TextView) findViewById(R.id.deadtime);
        sink_current= (TextView) findViewById(R.id.sink_current);
        source_current=(TextView) findViewById(R.id.source_current);

        calculateStats();
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        String status=sharedPreferences.getString("connection_status", "");
        if(status=="Synchronized")
        displayStats();
        else toast (getApplicationContext(), "Device not synchronized", Toast.LENGTH_SHORT);
    }

    private void displayStats() {
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        distance_covered.setText(sharedPreferences.getFloat("distance_covered", 0)+" m");
        speed_average.setText(sharedPreferences.getFloat("current_speed", 0)+" km/h");
        power_average.setText(sharedPreferences.getFloat("current_power", 0)+" W");
        energy_saved.setText(Float.toString(float_energy_saved)+" Wh");
        reduced_co2.setText(Float.toString(float_reduced_co2)+" g");
        calories_burned.setText(Float.toString(float_calories_burned)+" kcal");
        battery_voltage.setText(sharedPreferences.getFloat("battery_voltage", 0)+ " V");
        range_left.setText(sharedPreferences.getFloat("range", 0)+ " m");
        deadtime.setText(sharedPreferences.getFloat("set_deadtime", 0)+" ms");
        sink_current.setText(sharedPreferences.getFloat("set_sink_current", 0)+" mA");
        source_current.setText(sharedPreferences.getFloat("set_source_current", 0)+" mA");
    }

    private void calculateStats() {
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        float distance = sharedPreferences.getFloat("distance_covered", 0);
        float_energy_saved=distance/965;
        float_reduced_co2=distance/213;
        float_calories_burned=distance/90;
    }
}
