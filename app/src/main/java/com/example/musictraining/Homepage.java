package com.example.musictraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private TextView activity_subtext;
    private ModelPredict predictor;
    private MediaPlayer player;
    private SharedPreferences prefs;

    public void goToCalibrate(View view) {
        Intent intent = new Intent(Homepage.this, ModelTraining.class);
        startActivity(intent);
    }

    public void goToMusic(View view) {
        Intent intent = new Intent(Homepage.this, MusicChoice.class);
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
        activity_subtext = findViewById(R.id.activity_sub_text);
        activity_subtext.setText("");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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
                if (prefs.getLong("sitting", -1) == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("sittingname", ""));
                }
                activity_display.setText("Sitting/Idle");
                break;
            case "J":
                if (prefs.getLong("jumping", -1) == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("jumpingname", ""));
                }
                activity_display.setText("Jumping");
                break;
            case "R":
                if (prefs.getLong("running", -1) == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("runningname", ""));
                }
                activity_display.setText("Running");
                break;
            case "S":
                if (prefs.getLong("skipping", -1) == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("skippingname", ""));
                }
                activity_display.setText("Skipping");
                break;
            case "W":
                if (prefs.getLong("walking", -1) == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("walkingname", ""));
                }
                activity_display.setText("Walking");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i) {

    }





}
