package com.example.musictraining;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class Homepage extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private Sensor linAccelSensor;
    private Sensor accelSensor;
    private TextView activity_display;
    private ModelPredict predictor;

    public void goToCalibrate(View view) {
        Intent intent = new Intent(Homepage.this, ModelTraining.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        predictor = new ModelPredict();
        activity_display = findViewById(R.id.activity_text);
        activity_display.setText("Loading...");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);

        if (gyroSensor == null || linAccelSensor == null || accelSensor == null) {
            activity_display.setText("No sensors");
        }
        else {
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, linAccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String res = predictor.modelPredict(event);
        switch (res) {
            case "D":
                activity_display.setText("Sitting/Idle");
                break;
            case "J":
                activity_display.setText("Jumping");
                break;
            case "R":
                activity_display.setText("Running");
                break;
            case "S":
                activity_display.setText("Skipping");
                break;
            case "W":
                activity_display.setText("Walking");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i) {

    }





}
