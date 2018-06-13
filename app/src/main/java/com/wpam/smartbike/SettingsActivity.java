package com.wpam.smartbike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    String deviceData;

    Spinner deadtime_spinner;
    Spinner sink_spinner;
    Spinner source_spinner;

    String[] deadtime_list = new String[]{"010.0", "050.0", "100.0"};
    String[] sink_list = new String[]{"1000.0", "1500.0", "2000.0"};
    String[] source_list = new String[]{"0500.0", "1000.0", "1500.0"};


    ArrayAdapter<String> adapter_dead;
    ArrayAdapter<String> adapter_sink;
    ArrayAdapter<String> adapter_source;

    public void toast(AdapterView.OnItemSelectedListener context, String text, int duration) {
        Toast myToast = Toast.makeText((Context) context, text, duration);
        myToast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent previousIntent = getIntent();
        deviceData = previousIntent.getStringExtra("deviceData");


        deadtime_spinner = findViewById(R.id.spinner_deadtime);
        sink_spinner = findViewById(R.id.spinner_sinkcurrent);
        source_spinner = findViewById(R.id.spinner_sourcecurrent);

        adapter_dead = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deadtime_list);
        adapter_sink = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sink_list);
        adapter_source = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, source_list);

        deadtime_spinner.setAdapter(adapter_dead);
        sink_spinner.setAdapter(adapter_sink);
        source_spinner.setAdapter(adapter_source);


        deadtime_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("set_deadtime", Float.parseFloat(deadtime_list[position]));
                editor.putString("connection_status","Not synchronized");
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sink_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("set_sink_current", Float.parseFloat(sink_list[position]));
                editor.putString("connection_status","Not synchronized");
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        source_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("set_source_current", Float.parseFloat(source_list[position]));
                editor.putString("connection_status","Not synchronized");
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
}
