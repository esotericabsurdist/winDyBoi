package com.spaceshipfreehold.windyboi;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class Winch extends Thread {

    WeakReference<Context> mContext;
    BluetoothAdapter bluetoothAdapter;
    volatile boolean in = false;
    volatile boolean out = false;

    Winch(Context context){
        mContext = new WeakReference<>(context);
    }

    public void connect(){
        if(bluetoothAdapter == null){
            // Ensure that we have the singleton
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if(bluetoothAdapter != null){
            if(bluetoothAdapter.isEnabled()){

            } else {
                Toast.makeText(mContext.get(), "Enable Bluetooth!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext.get(), "Failed to get bluetooth adapter", Toast.LENGTH_LONG).show();
        }
    }

    public void out(){
        this.in = false;
        this.out = true;
    }

    public void in(){
        this.out = false;
        this.in = true;
    }

    public void  brake(){
        this.in = false;
        this.out = false;
    }

    @Override
    public void run() {
        while(true) {
            try {
                if(in){
                    // TODO: send to mWinch radio.
                    sleep(100);
                    Log.d("derp", "whirrrrrrrr ...... innnnn");
                } else if (out) {
                    // TODO: send to mWinch radio.
                    sleep(100);
                    Log.d("derp", "whirrrrrr out");
                } else {
                    // Don't do anything.
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
