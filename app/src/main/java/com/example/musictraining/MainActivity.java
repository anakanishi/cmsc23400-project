package com.example.musictraining;

// A lot of this code was written with the help of the google developer training gitbooks and android documentation


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView tv1=null;
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private Sensor linAccelSensor;
    private Sensor accelSensor;

    // Individual light and proximity sensors.
    //private Sensor mSensorLight;
    // TextViews to display current sensor values
    //private TextView mTextSensorLight;

    private boolean record = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String sensor_error = getResources().getString(R.string.no_sensor);
        tv1 = findViewById(R.id.textView2);

        /* Uncomment this to see a list of sensors that is on the phone
        List<Sensor> mList= mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 1; i < mList.size(); i++) {
            tv1.append(mList.get(i).getName() + "\n");
        }*/

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (gyroSensor == null)
            tv1.append(sensor_error);

        if (linAccelSensor == null)
            tv1.append(sensor_error);

        if (accelSensor == null)
            tv1.append(sensor_error);

        /*mTextSensorLight = findViewById(R.id.label_light);
        mSensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mSensorLight == null) {
            mTextSensorLight.setText(sensor_error);
        }*/




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

        if (gyroSensor != null) {
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }



//        if (mSensorLight != null) {
//            sensorManager.registerListener(this, mSensorLight,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
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
                tv1.append("creation of file failed\n");
            }
        }
        return file;
    }

    /** Called when the user touches the button */
    public void sendToFile(View view)
    {
        // Do something in response to button click

        tv1.append("button clicked!");

        // Waits five seconds before we actually set to true
        // This will give enough time to put the phone back in pocket and continue motion
        // Got code from: https://stackoverflow.com/questions/31041884/execute-function-after-5-seconds-in-android

        if (record)
            record = false;
        else {
            tv1.append("writing to file\n");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv1.append("set to true\n");
                    record = true;
                }
            }, 5000);
            //record = true;
        }

        if (record)
            tv1.append("true\n");
        else
            tv1.append("false\n");

        // Then we want to only record for 10 seconds (it is 15000 because we have the five second initial delay)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv1.append("set to false\n");
                record = false;
            }
        }, 15000);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            // Event came from the light sensor. - Honestly, this will probably never trigger again, but the light code in general is good for debugging purposes
            case Sensor.TYPE_LIGHT:
                float currentValue = event.values[0];
                // Handle light sensor
//                mTextSensorLight.setText(getResources().getString(
//                        R.string.label_light, currentValue));
                if (record){
                    File file = accessFile("light_data.txt");
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(file, true /*append*/));
                        writer.write("light value: " + currentValue + ", timestamp: " + event.timestamp + "\n");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        tv1.append("something went wrong writing to file!");
                    }
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                float xAxis = event.values[0];
                float yAxis = event.values[1];
                float zAxis = event.values[2];
                if (record){
                    File file = accessFile("gyro_data.txt");
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(file, true /*append*/));
                        writer.write("timestamp: " + event.timestamp + ", xAxis: " + xAxis + ", yAxis: " + yAxis + ", zAxis" + zAxis + "\n");
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