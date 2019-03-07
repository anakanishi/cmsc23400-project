package com.example.musictraining;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import java.io.IOException;

import static android.media.AudioAttributes.CONTENT_TYPE_MUSIC;
import static android.media.AudioAttributes.USAGE_MEDIA;

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
    private AudioAttributes audio_attrib = new AudioAttributes.Builder().setUsage(USAGE_MEDIA).setContentType(CONTENT_TYPE_MUSIC).build();
    private int current_prediction;

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
        player = new MediaPlayer();
        current_prediction = -1;

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
        long music_id;
        switch (res) {
            case "D":
                music_id = prefs.getLong("sitting", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("sittingname", ""));
                    // This is checking if hte song needs to be changed
                    if (current_prediction != 0){
                        // If the song needs to be changed, we stop the current song.
                        if (current_prediction != -1) {
                            player.stop();
                            player.reset();
                        }
                        current_prediction = 0;
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                        player.setAudioAttributes(audio_attrib);
                        try {
                            player.setDataSource(getApplicationContext(), contentUri);
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // To be honest there is probably a better way to indicate error.
                            activity_subtext.setText("ERROR - MUSIC CANNOT PLAY");
                        }
                    }
                }
                activity_display.setText("Sitting/Idle");
                break;
            case "J":
                music_id = prefs.getLong("jumping", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("jumpingname", ""));
                    if (current_prediction != 1){
                        // If the song needs to be changed, we stop the current song.
                        if (current_prediction != -1) {
                            player.stop();
                            player.reset();
                        }
                        current_prediction = 1;
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                        player.setAudioAttributes(audio_attrib);
                        try {
                            player.setDataSource(getApplicationContext(), contentUri);
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // To be honest there is probably a better way to indicate error.
                            activity_subtext.setText("ERROR - MUSIC CANNOT PLAY");
                        }
                    }
                }
                activity_display.setText("Jumping");
                break;
            case "R":
                music_id = prefs.getLong("running", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("runningname", ""));
                    if (current_prediction != 2){
                        // If the song needs to be changed, we stop the current song.
                        if (current_prediction != -1) {
                            player.stop();
                            player.reset();
                        }
                        current_prediction = 2;
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                        player.setAudioAttributes(audio_attrib);
                        try {
                            player.setDataSource(getApplicationContext(), contentUri);
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // To be honest there is probably a better way to indicate error.
                            activity_subtext.setText("ERROR - MUSIC CANNOT PLAY");
                        }
                    }
                }
                activity_display.setText("Running");
                break;
            case "S":
                music_id = prefs.getLong("skipping", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("skippingname", ""));
                    if (current_prediction != 3){
                        // If the song needs to be changed, we stop the current song.
                        if (current_prediction != -1) {
                            player.stop();
                            player.reset();
                        }
                        current_prediction = 3;
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                        player.setAudioAttributes(audio_attrib);
                        try {
                            player.setDataSource(getApplicationContext(), contentUri);
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // To be honest there is probably a better way to indicate error.
                            activity_subtext.setText("ERROR - MUSIC CANNOT PLAY");
                        }
                    }
                }
                activity_display.setText("Skipping");
                break;
            case "W":
                music_id = prefs.getLong("walking", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    activity_subtext.setText(prefs.getString("walkingname", ""));
                    if (current_prediction != 4){
                        // If the song needs to be changed, we stop the current song.
                        if (current_prediction != -1) {
                            player.stop();
                            player.reset();
                        }
                        current_prediction = 4;
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                        try {
                            player.setDataSource(getApplicationContext(), contentUri);
                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // To be honest there is probably a better way to indicate error.
                            activity_subtext.setText("ERROR - MUSIC CANNOT PLAY");
                        }
                    }
                }
                activity_display.setText("Walking");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i) {

    }





}
