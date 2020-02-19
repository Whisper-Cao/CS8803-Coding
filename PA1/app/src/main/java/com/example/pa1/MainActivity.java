package com.example.pa1;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    Button mReceiveButton;
    Button mStopButton;
    SensorService mSensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReceiveButton = (Button) findViewById(R.id.receive);
        mStopButton = (Button) findViewById(R.id.stop);
        mSensorService = new SensorService();

        mReceiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                mReceiveButton.setBackgroundColor(0xFF336600);
                mStopButton.setBackgroundColor(0xFFE0E0E0);
                mSensorService.startSensor(getApplicationContext());

            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiveButton.setBackgroundColor(0xFFE0E0E0);
                mStopButton.setBackgroundColor(0xFF336600);
                mSensorService.stopSensor();

            }
        });

    }
}
