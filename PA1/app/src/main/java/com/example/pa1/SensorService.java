package com.example.pa1;

import android.app.Activity;
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
    private long timeLastSent = -10000;
    private int sendingDuration = 1; //ms
    private int sendBatchSize = 20;
    private String tosend = "";
    private int tosendRows = 0;

    FileOutputStream fos = null;

    private DataSocket mDataSocket;

    private boolean useVisualization = true;
    private long firsttime;
    private boolean first = true;


    public void startSensor(Context context){
        initialize(context);
        File path = context.getExternalFilesDir(null);
        File file = new File(path, csvFile);
        first = true;
        try{
            fos = new FileOutputStream(file);
        }catch (Exception e){
            Log.d(TAG, "Error in opening file!");
        }

        mSensorManager.registerListener(this, mAcceleration, mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagnetometer, mSensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mLightSensor, mSensorManager.SENSOR_DELAY_FASTEST);

    }

    public void stopSensor(){
        Log.d(TAG, "stopSensor()");
        mSensorManager.unregisterListener(this);
        first = true;
        try{
            fos.close();
        }catch (Exception e){
            Log.d(TAG, "No file to close");
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            sensorData[1] = event.values[0];
            sensorData[2] = event.values[1];
            sensorData[3] = event.values[2];
            count++;
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            sensorData[4] = event.values[0];
            sensorData[5] = event.values[1];
            sensorData[6] = event.values[2];
            count++;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            sensorData[7] = event.values[0];
            sensorData[8] = event.values[1];
            sensorData[9] = event.values[2];
        }
        if(event.sensor.getType() == Sensor.TYPE_LIGHT ) {
            sensorData[10] = event.values[0];
        }

        count = 0;

        if(first){
            firsttime = event.timestamp / 100000L;
        }
        sensorData[0] = event.timestamp / 100000L - firsttime;

        first = false;
        writeToCSV();
        /*
        for(double data: sensorData){
            tosend += (double)Math.round(data * 1000d) / 1000d;
            tosend += ",";
        }
        tosend += "\n";
        tosend = "";

         */
        for(int i = 0; i < 11; i++){
            sensorData[i] = 0;
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

    private void sentToPC(){
        byte [] b = tosend.getBytes();
        mDataSocket.write(b);
    }



    public void initialize(Context context){
        //mDataSocket = new DataSocket(context);
        //mDataSocket.buildConnection();
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