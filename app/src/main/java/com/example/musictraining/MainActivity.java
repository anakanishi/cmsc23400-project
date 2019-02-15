package com.example.musictraining;

// A lot of this code was written with the help of the google developer training gitbooks and android documentation


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import android.app.Activity;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView tv1=null;
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private Sensor linAccelSensor;
    private Sensor accelSensor;
    public static final String LOG_TAG = "DIRECTORY-STATUS";

    // Individual light and proximity sensors.
    private Sensor mSensorLight;

    // TextViews to display current sensor values
    private TextView mTextSensorLight;

    private boolean record = false;
    //private File file = accessFile("light_data.txt");

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv1 = (TextView) findViewById(R.id.textView2);
        //tv1.setVisibility(View.GONE);





        /* Uncomment this to see a list of sensors that is on the phone
        List<Sensor> mList= mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 1; i < mList.size(); i++) {
            tv1.append(mList.get(i).getName() + "\n");
        }*/

        // Creating the sensors (hopefully)
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mTextSensorLight = findViewById(R.id.label_light);
        mSensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        String sensor_error = getResources().getString(R.string.no_sensor);
        if (mSensorLight == null) {
            mTextSensorLight.setText(sensor_error);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSensorLight != null) {
            sensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File accessFile(String fileName) {
        // Get the directory for the public files directory.
        File file = new File(this.getExternalFilesDir(null), fileName);
        // If there is no file, it creates one
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                tv1.append("did not work\n");
            }
        }
        return file;
    }

    /** Called when the user touches the button */
    public void sendToFile(View view)
    {
        // Do something in response to button click

        if (record == true)
            record = false;
        else {
            tv1.append("writing to file\n");
            record = true;
        }

        if (record == true)
            tv1.append("true\n");
        else
            tv1.append("false\n");

        // File stored in MyFiles > com.example.musictraining
//        String fileName = "test_file1.txt";
//
//        File file = accessFile(fileName);
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter(file, true /*append*/));
//            writer.write("This is a test file.");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            tv1.append("something went wrong writing to file!");
//        }
        tv1.append("yay");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float currentValue = event.values[0];
        switch (sensorType) {
            // Event came from the light sensor.
            case Sensor.TYPE_LIGHT:
                // Handle light sensor
                mTextSensorLight.setText(getResources().getString(
                        R.string.label_light, currentValue));
                if (record == true){
                    File file = accessFile("light_data.txt");
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(file, true /*append*/));
                        writer.write(currentValue + "\n");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        tv1.append("something went wrong writing to file!");
                    }
                }
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}