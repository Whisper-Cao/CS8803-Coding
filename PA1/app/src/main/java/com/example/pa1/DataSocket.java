package com.example.pa1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DataSocket {
    private static final String TAG = DataSocket.class.getName();;

    private String deviceNameToConnect = "LAPTOP-LU6BV5Q9";
    private BluetoothDevice mPairedDevice;

    private final int READ_BUFFER_SIZE = 35;//

    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mAdapter;

    private BluetoothSocket mSocket;

    InputStream mInputStream = null;
    OutputStream mOutputStream = null;

    private WriteThread mWriteThread;

    private Context mContext;

    //private boolean isBtActive = false;

    public DataSocket(Context context){
        mContext = context;
    }

    public void stopConnection(){
        mWriteThread.interrupt();
        mWriteThread = null;
        try{
            if(mSocket.isConnected()){
                mSocket.close();
            }
        }catch (Exception e){
            int x = 1;
        }

    }

    public void buildConnection(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice [] s;
        List<BluetoothDevice> pairedDevices = Arrays.asList(mAdapter.getBondedDevices().toArray(new BluetoothDevice[0]));
        for(BluetoothDevice bd : pairedDevices){
            if(bd.getName().equals(deviceNameToConnect)){
                mPairedDevice = bd;
            }
        }
        try {

            mWriteThread = new WriteThread();
            mWriteThread.start();
        }catch (Exception e){
            Log.d(TAG, "No bluetooth device is found by given name: " + deviceNameToConnect);
        }

    }

    public void BtConnect(){
        try {
            mSocket = mPairedDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket = (BluetoothSocket) mPairedDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mPairedDevice, 1);
                mSocket.connect();
            } catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
        }

        if(mSocket.isConnected()){
            try{
                mOutputStream = mSocket.getOutputStream();
            }catch (IOException e){
                Log.e("","Exception of .. after connected");
                e.printStackTrace();
            }
        } else {
            SystemClock.sleep(2000);
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("", "Closing failed.");
            }
        }
    }

    public boolean isConnectionActive(){
        return (mSocket != null) && (mSocket.isConnected());
    }

    private class WriteThread extends Thread{
        public void run(){
            while((mSocket == null || !mSocket.isConnected()) && !Thread.currentThread().isInterrupted()){
                BtConnect();
            }
        }
    }



    public void write(byte [] writeData){
        try{
            mOutputStream.write(writeData);
        }catch(Exception e){
            Log.d(TAG, "Failed to write into outputstream.");
        }

    }
}
