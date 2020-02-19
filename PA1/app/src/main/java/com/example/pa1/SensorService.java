package com.example.pa1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SensorService implements SensorEventListener{
    private SensorManager mSensorManager;

    private Sensor mAcceleration;
    private Sensor mGyroscope;
    private Sensor mMagnetometer;
    private Sensor mLightSensor;

    private static final String TAG = SensorService.class.getName();

    private boolean flagAcc = false; //whether this sensor has been collected in this iteration
    private boolean flagGy = false;
    private boolean flagMag = false;
    private boolean flagLight = false;
    private int count = 0; //The count of sensors whose data have been collected in current iteration

    private double [] sensorData = new double[11];

    private String csvFile = "sensor_data.csv";

    private long timeBegin= System.currentTimeMillis();
    FileOutputStream fos = null;

    public void startSensor(Context context){
        initialize(context);
        mSensorManager.registerListener(this, mAcceleration, mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagnetometer, mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mLightSensor, mSensorManager.SENSOR_DELAY_FASTEST);
        File path = context.getExternalFilesDir(null);
        File file = new File(path, csvFile);
        try{
            fos = new FileOutputStream(file);
        }catch (Exception e){
            Log.d(TAG, "Error in opening file!");
        }
    }

    public void stopSensor(){
        Log.d(TAG, "stopSensor()");
        mSensorManager.unregisterListener(this);
        try{
            fos.close();
        }catch (Exception e){
            Log.d(TAG, "No file to close");
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !flagAcc){
            sensorData[1] = event.values[0];
            sensorData[2] = event.values[1];
            sensorData[3] = event.values[2];
            flagAcc = true;
            count++;
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE && !flagGy) {
            sensorData[4] = event.values[0];
            sensorData[5] = event.values[1];
            sensorData[6] = event.values[2];
            flagGy = true;
            count++;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && !flagMag) {
            sensorData[7] = event.values[0];
            sensorData[8] = event.values[1];
            sensorData[9] = event.values[2];
            flagMag = true;
            count++;
        }
        if(event.sensor.getType() == Sensor.TYPE_LIGHT && !flagLight) {
            sensorData[10] = event.values[0];
            flagLight = true;
            count++;
        }
        if(count == 4) {
            count = 0;
            flagAcc = false;
            flagGy = false;
            flagMag = false;
            flagLight = false;
            sensorData[0] = System.currentTimeMillis() - timeBegin;
            writeToCSV();
        }
    }

    private void writeToCSV(){
        String tosave = "";
        for(int i = 0; i < sensorData.length; i++){
            tosave += Double.toString(sensorData[i]) + " ";
        }
        tosave += "\n";
        try{
            fos.write(tosave.getBytes());
        }catch (Exception e){
            Log.d(TAG,"Write failed");
        }

    }



    public void initialize(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope= mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "AnalysisData.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

}