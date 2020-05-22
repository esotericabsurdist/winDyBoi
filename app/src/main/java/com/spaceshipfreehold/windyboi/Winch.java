package com.spaceshipfreehold.windyboi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;

public class Winch extends Thread {

    private static Winch mInstance;
    private WeakReference<Context> mContext;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private IWinchConnectionListener mListener;

    private static String WINCH_NAME = "HC-06";
    private static byte IN_COMMAND = 'i';
    private static byte OUT_COMMAND = 'o';

    volatile boolean mIn = false;
    volatile boolean mOut = false;
    volatile boolean mConnected = false;

    private Winch(){}

    public static Winch getInstance(Context context){
        if(mInstance == null){
            mInstance = new Winch();
            mInstance.mContext = new WeakReference<>(context);
        }
        return mInstance;
    }

    public void setListener(@NonNull IWinchConnectionListener listener){
        mListener = listener;
    }

    public boolean connect(){
        if(mBluetoothAdapter == null){
            // Ensure that we have the adapter singleton.fg
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if(mBluetoothAdapter != null){
            if(mBluetoothAdapter.isEnabled()){
                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : devices){
                    if(device.getName().toLowerCase().equals(WINCH_NAME.toLowerCase())){
                        // We found our winch by name.
                        mBluetoothDevice = device;
                        break;
                    }
                }

                if(mBluetoothDevice == null){
                    if(mListener!= null){
                        mListener.onWinchNotFound();
                        return false;
                    }
                }

                if(mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){

                    // UUID from Google's JavaDoc for Serial BT, see createInsecureRfcommSocketToServiceRecord(...) source.
                    UUID serialConnectionUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    try {
                        mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(serialConnectionUUID);
                        mBluetoothSocket.connect();
                        if(mBluetoothSocket.isConnected()){
                            mConnected = true;
                            if(mListener != null){
                                mListener.onConnected();
                            }
                            return true;
                        }
                    } catch (Exception e) {
                        if(mListener != null){
                            mListener.onSocketConnectionFailed();
                        }
                        return false;
                    }
                }

            } else {
                if(mListener != null){
                    mListener.onBluetoothNotEnabled();
                }
                return false;
            }
        } else {
            if(mListener != null){
                mListener.onNoBluetoothAdapterFound();
            }
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
                // Optimal values:
                // Galaxy S7: 55
                // Moto G7:

                // TODO: Allow for this value to be set in the application by a slider.
                sleep(74); // Empirically derived value based on rate of buffer consumption on Arduino/BT module.
                if(mIn){
                    mBluetoothSocket.getOutputStream().write(IN_COMMAND);
                } else if (mOut) {
                    mBluetoothSocket.getOutputStream().write(OUT_COMMAND);
                } else if(!mBluetoothSocket.isConnected() && mConnected) {
                    mConnected = false;
                    if (mListener != null) {
                        mListener.onDisconnected();
                    }
                } else if(mBluetoothDevice == null){ // Maybe this works?/
                    mListener.onDisconnected();
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
