package com.spaceshipfreehold.windyboi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

public class Winch extends Thread {

    private WeakReference<Context> mContext;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private static byte IN_COMMAND = 'i';
    private static byte OUT_COMMAND = 'o';

    volatile boolean mIn = false;
    volatile boolean mOut = false;

    Winch(Context context){
        mContext = new WeakReference<>(context);
    }

    public boolean connect(){
        if(mBluetoothAdapter == null){
            // Ensure that we have the singleton
            Log.d("derp", "bluetooth adpater is null");
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if(mBluetoothAdapter != null){
            if(mBluetoothAdapter.isEnabled()){
                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : devices){
                    if(device.getName().toLowerCase().equals("windyboi")){
                        // We found our winch by name.
                        Toast.makeText(mContext.get(), "Found " + device.getName(), Toast.LENGTH_SHORT).show();
                        mBluetoothDevice = device;
                        break;
                    }
                }

                if(mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    // UUID from Google's JavaDoc for Serial BT, see createInsecureRfcommSocketToServiceRecord(...) source.
                    UUID serialConnectionUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    try {
                        mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(serialConnectionUUID);
                        mBluetoothSocket.connect();
                        if(mBluetoothSocket.isConnected()){
                            Toast.makeText(mContext.get(), "Connected!", Toast.LENGTH_LONG).show();
                            return true;
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext.get(), "CONNECTION FAILED", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

            } else {
                Toast.makeText(mContext.get(), "Enable Bluetooth!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(mContext.get(), "Failed to get bluetooth adapter. Enable Bluetooth!", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    public void out(){
        this.mIn = false;
        this.mOut = true;
    }

    public void in(){
        this.mOut = false;
        this.mIn = true;
    }

    public void  brake(){
        this.mIn = false;
        this.mOut = false;
    }

    @Override
    public void run() {
        while(true) {
            try {
                // TODO: Determine if sleep is required here. to not over fill the buffer on the BT chip.
                sleep(60);
                if(mIn){
                    mBluetoothSocket.getOutputStream().write(IN_COMMAND);
                } else if (mOut) {
                    mBluetoothSocket.getOutputStream().write(OUT_COMMAND);
                } else {
                    // Don't do anything.
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
