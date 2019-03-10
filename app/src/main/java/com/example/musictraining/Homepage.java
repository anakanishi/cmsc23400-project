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
    private int currently_playing;
    private int consecutiveNumPredictions = 0;
    private int ticksBeforeSwitch = 10;

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
        currently_playing = -1;

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

    private void playMusic(Uri contentUri) {
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

    private void stopMusic() {
        player.stop();
        player.reset();
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
                    // This is checking if hte song needs to be changed
                    if (current_prediction != 0){
                        if (current_prediction == -1) {
                            // First initialization of player
                            activity_subtext.setText(prefs.getString("sittingname", ""));
                            activity_display.setText("Sitting/Idle");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                            currently_playing = 0;
                        }
                        consecutiveNumPredictions = 0;
                        current_prediction = 0;
                    }
                    else {
                        consecutiveNumPredictions++;
                        if (consecutiveNumPredictions > ticksBeforeSwitch && current_prediction != currently_playing) {
                            // Stop the current song
                            stopMusic();
                            activity_subtext.setText(prefs.getString("sittingname", ""));
                            activity_display.setText("Sitting/Idle");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                        }
                    }
                }
                break;
            case "J":
                music_id = prefs.getLong("jumping", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    if (current_prediction != 1){
                        if (current_prediction == -1) {
                            // First initialization of player
                            activity_subtext.setText(prefs.getString("jumpingname", ""));
                            activity_display.setText("Jumping");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                            currently_playing = 1;
                        }
                        consecutiveNumPredictions = 0;
                        current_prediction = 1;
                    }
                    else {
                        consecutiveNumPredictions++;
                        if (consecutiveNumPredictions > ticksBeforeSwitch && current_prediction != currently_playing) {
                            // Stop the current song
                            stopMusic();
                            activity_subtext.setText(prefs.getString("jumpingname", ""));
                            activity_display.setText("Jumping");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                        }
                    }
                }
                break;
            case "R":
                music_id = prefs.getLong("running", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    if (current_prediction != 2){
                        if (current_prediction == -1) {
                            // First initialization of player
                            activity_subtext.setText(prefs.getString("runningname", ""));
                            activity_display.setText("Running");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                            currently_playing = 2;
                        }
                        consecutiveNumPredictions = 0;
                        current_prediction = 2;
                    }
                    else {
                        consecutiveNumPredictions++;
                        if (consecutiveNumPredictions > ticksBeforeSwitch && current_prediction != currently_playing) {
                            // Stop the current song
                            stopMusic();
                            activity_subtext.setText(prefs.getString("runningname", ""));
                            activity_display.setText("Running");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                        }
                    }
                }
                break;
            case "S":
                music_id = prefs.getLong("skipping", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    if (current_prediction != 3){
                        if (current_prediction == -1) {
                            // First initialization of player
                            activity_subtext.setText(prefs.getString("skippingname", ""));
                            activity_display.setText("Skipping");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                            currently_playing = 3;
                        }
                        consecutiveNumPredictions = 0;
                        current_prediction = 3;
                    }
                    else {
                        consecutiveNumPredictions++;
                        if (consecutiveNumPredictions > ticksBeforeSwitch && current_prediction != currently_playing) {
                            // Stop the current song
                            stopMusic();
                            activity_subtext.setText(prefs.getString("skippingname", ""));
                            activity_display.setText("Skipping");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                        }
                    }
                }
                break;
            case "W":
                music_id = prefs.getLong("walking", -1);
                if (music_id == -1) {
                    activity_subtext.setText("No music set");
                }
                else {
                    if (current_prediction != 4){
                        if (current_prediction == -1) {
                            // First initialization of player
                            activity_subtext.setText(prefs.getString("walkingname", ""));
                            activity_display.setText("Walking");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                            currently_playing = 4;
                        }
                        consecutiveNumPredictions = 0;
                        current_prediction = 4;
                    }
                    else {
                        consecutiveNumPredictions++;
                        if (consecutiveNumPredictions > ticksBeforeSwitch && current_prediction != currently_playing) {
                            // Stop the current song
                            stopMusic();
                            activity_subtext.setText(prefs.getString("walkingname", ""));
                            activity_display.setText("Walking");
                            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music_id);
                            playMusic(contentUri);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i) {

    }





}
